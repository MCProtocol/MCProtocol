package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.net.PacketEncryption
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.PassthroughPacket
import io.netty.channel.Channel
import java.lang.reflect.Constructor

/**
 * The main juice
 * @author Cubxity
 * @since 7/20/2019
 */
class ProtocolSession(val side: Side, val channel: Channel) {
    companion object {
        val packetConstructors = mutableMapOf<Class<out Packet>, Constructor<out Packet>>()
    }

    var encryption: PacketEncryption? = null
    var compressionThreshold: Int? = null
    val incomingPackets = mutableMapOf<Int, Class<out Packet>>()
    val outgoingPackets = mutableMapOf<Int, Class<out Packet>>()
    var subProtocol = SubProtocol.HANDSHAKE


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