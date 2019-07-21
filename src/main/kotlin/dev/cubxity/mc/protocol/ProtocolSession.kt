package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.net.PacketEncryption
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCompression
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.PassthroughPacket
import dev.cubxity.mc.protocol.packets.handshake.client.HandshakePacket
import dev.cubxity.mc.protocol.packets.login.client.EncryptionResponsePacket
import dev.cubxity.mc.protocol.packets.login.client.LoginPluginResponsePacket
import dev.cubxity.mc.protocol.packets.login.client.LoginStartPacket
import dev.cubxity.mc.protocol.packets.login.server.*
import dev.cubxity.mc.protocol.packets.status.client.StatusPingPacket
import dev.cubxity.mc.protocol.packets.status.client.StatusQueryPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusPongPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusResponsePacket
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.LoggerFactory
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers
import java.lang.reflect.Constructor
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The main juice
 * @author Cubxity
 * @since 7/20/2019
 */
class ProtocolSession @JvmOverloads constructor(
    val side: Side,
    val channel: Channel,
    val incomingVersion: ProtocolVersion = ProtocolVersion.V1_14_4,
    val outgoingVersion: ProtocolVersion = ProtocolVersion.V1_14_4
) : SimpleChannelInboundHandler<Packet>() {
    companion object {
        val packetConstructors = mutableMapOf<Class<out Packet>, Constructor<out Packet>>()
    }

    val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Packet encryption
     * https://wiki.vg/Protocol_Encryption
     */
    var encryption: PacketEncryption? = null

    /**
     * Protocol compression
     * https://wiki.vg/Protocol#With_compression
     */
    var compressionThreshold: Int? = null
        set(value) {
            with(channel.pipeline()) {
                if (value != null) {
                    if (get("compression") == null)
                        addBefore("codec", "compression", TcpPacketCompression(this@ProtocolSession))
                } else if (get("compression") != null)
                    remove("compression")
            }
            field = value
        }

    /**
     * Map of registered incoming packets
     */
    val incomingPackets = mutableMapOf<Int, Class<out Packet>>()

    /**
     * Map of registered outgoing packets
     */
    val outgoingPackets = mutableMapOf<Int, Class<out Packet>>()

    /**
     * Sub protocol state
     * - https://wiki.vg/Protocol#Handshaking
     * - https://wiki.vg/Protocol#Status
     * - https://wiki.vg/Protocol#Login
     * - https://wiki.vg/Protocol#Play
     */
    var subProtocol = SubProtocol.HANDSHAKE

    val packetProcessor = EmitterProcessor.create<Packet>()

    val packetScheduler = Schedulers.newSingle("Protocol-PacketManager", true)

    val packetSink = packetProcessor.sink(FluxSink.OverflowStrategy.BUFFER)

    /**
     * Do not use this unless it's required
     * The listeners will be called in the thread that [channelRead0] is called from
     * @see onPacket
     */
    val syncListeners = CopyOnWriteArrayList<(Packet) -> Unit>()


    /**
     * Applies all default settings
     */
    fun applyDefaults() {
        registerDefaults()
        when (side) {
            Side.CLIENT -> defaultClientHandler()
            Side.SERVER -> defaultServerHandler()
        }
    }

    fun defaultServerHandler() {
        syncListeners += {
            when (it) {
                is HandshakePacket -> {
                    subProtocol = when (it.intent) {
                        HandshakePacket.Intent.LOGIN -> SubProtocol.LOGIN
                        HandshakePacket.Intent.STATUS -> SubProtocol.STATUS
                    }
                    registerDefaults()
                }
            }
        }
    }

    fun defaultClientHandler() {
    }

    /**
     * Registers default packets for current [subProtocol]
     * @param clear to clear current registered packets in [incomingPackets] and [outgoingPackets]
     */
    @JvmOverloads
    fun registerDefaults(clear: Boolean = true) {
        if (clear) {
            incomingPackets.clear()
            outgoingPackets.clear()
        }
        // from client
        val client = when (side) {
            Side.CLIENT -> outgoingPackets
            Side.SERVER -> incomingPackets
        }
        // from server
        val server = when (side) {
            Side.CLIENT -> incomingPackets
            Side.SERVER -> outgoingPackets
        }
        when (subProtocol) {
            SubProtocol.HANDSHAKE -> client[0x00] = HandshakePacket::class.java
            SubProtocol.STATUS -> {
                server[0x00] = StatusResponsePacket::class.java
                server[0x01] = StatusPongPacket::class.java

                client[0x00] = StatusQueryPacket::class.java
                client[0x01] = StatusPingPacket::class.java
            }
            SubProtocol.LOGIN -> {
                server[0x00] = LoginDisconnectPacket::class.java
                server[0x01] = EncryptionRequestPacket::class.java
                server[0x02] = LoginSuccessPacket::class.java
                server[0x03] = SetCompressionPacket::class.java
                server[0x04] = LoginPluginRequestPacket::class.java

                client[0x00] = LoginStartPacket::class.java
                client[0x01] = EncryptionResponsePacket::class.java
                client[0x02] = LoginPluginResponsePacket::class.java
            }
            SubProtocol.GAME -> {
            }
        }
    }

    /**
     * Prints every packet received
     * This is used for debugging
     * NOTE: [logger]'s level is required to be at DEBUG
     */
    fun wiretap(): ProtocolSession {
        onPacket<Packet>()
            .subscribe { logger.debug("[$side]: ${it.javaClass.simpleName}") }
        return this
    }

    inline fun <reified T : Packet> onPacket() =
        packetProcessor.publishOn(packetScheduler)
            .ofType(T::class.java)

    fun createOutgoingPacketById(id: Int): Packet {
        val p = outgoingPackets[id] ?: return PassthroughPacket(id)
        val c = packetConstructors.computeIfAbsent(p) { p.getConstructor().apply { isAccessible = true } }
        return c.newInstance()
    }

    fun createIncomingPacketById(id: Int): Packet {
        val p = incomingPackets[id] ?: return PassthroughPacket(id)
        val c = packetConstructors.computeIfAbsent(p) { p.getConstructor().apply { isAccessible = true } }
        return c.newInstance()
    }

    fun getOutgoingId(packet: Packet) =
        outgoingPackets.keys.elementAtOrElse(outgoingPackets.values.indexOf(packet::class.java)) { (packet as? PassthroughPacket)?.id }

    fun getIncomingId(packet: Packet) =
        incomingPackets.keys.elementAtOrElse(incomingPackets.values.indexOf(packet::class.java)) { (packet as? PassthroughPacket)?.id }

    fun send(packet: Packet) {
        channel.writeAndFlush(packet)
        //TODO: Packet sent event
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        syncListeners.forEach { it(packet) }
        packetSink.next(packet)
    }

    enum class Side {
        CLIENT,
        SERVER
    }

    enum class SubProtocol {
        HANDSHAKE,
        STATUS,
        LOGIN,
        GAME
    }
}