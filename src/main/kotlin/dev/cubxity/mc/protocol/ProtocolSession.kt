package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.net.PacketEncryption
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCompression
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.PassthroughPacket
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.reactor.asFlux
import reactor.core.publisher.Flux
import java.lang.reflect.Constructor

/**
 * The main juice
 * @author Cubxity
 * @since 7/20/2019
 */
class ProtocolSession(val side: Side, val channel: Channel) : SimpleChannelInboundHandler<Packet>() {
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

    val packetBroadcast = ConflatedBroadcastChannel<Packet>()


    /**
     * Applies all default settings
     */
    fun applyDefaults() {

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
        when (subProtocol) {
            SubProtocol.HANDSHAKE -> {

            }
            SubProtocol.STATUS -> when (side) {
                Side.CLIENT -> {

                }
                Side.SERVER -> {

                }
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

    fun <T : Packet> onPacket() = packetBroadcast.openSubscription().asFlux() as Flux<T>

    fun createOutgoingPacketById(id: Int): Packet {
        val p = outgoingPackets[id] ?: return PassthroughPacket(id)
        val c = packetConstructors.computeIfAbsent(p) { p.getConstructor() }
        return c.newInstance()
    }

    fun createIncomingPacketById(id: Int): Packet {
        val p = incomingPackets[id] ?: return PassthroughPacket(id)
        val c = packetConstructors.computeIfAbsent(p) { p.getConstructor() }
        return c.newInstance()
    }

    fun getOutgoingId(packet: Packet) =
        outgoingPackets.keys.elementAtOrElse(outgoingPackets.values.indexOf(packet::class.java)) { (packet as? PassthroughPacket)?.id }

    fun getIncomingId(packet: Packet) =
        incomingPackets.keys.elementAtOrElse(incomingPackets.values.indexOf(packet::class.java)) { (packet as? PassthroughPacket)?.id }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        packetBroadcast.offer(packet)
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