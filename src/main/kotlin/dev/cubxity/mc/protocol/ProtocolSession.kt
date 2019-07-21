package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.net.PacketEncryption
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCompression
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.PassthroughPacket
import dev.cubxity.mc.protocol.packets.handshake.client.HandshakePacket
import dev.cubxity.mc.protocol.packets.status.client.StatusPingPacket
import dev.cubxity.mc.protocol.packets.status.client.StatusQueryPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusPongPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusResponsePacket
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers
import java.lang.reflect.Constructor

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
        onPacket<HandshakePacket>()
            .next()
            .subscribe {
                subProtocol = when (it.intent) {
                    HandshakePacket.Intent.LOGIN -> SubProtocol.LOGIN
                    HandshakePacket.Intent.STATUS -> SubProtocol.STATUS
                }
                registerDefaults()
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
            SubProtocol.LOGIN -> when (side) {
                Side.CLIENT -> {

                }
                Side.SERVER -> {

                }
            }
            SubProtocol.GAME -> when (side) {
                Side.CLIENT -> {

                }
                Side.SERVER -> {

                }
            }
        }
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