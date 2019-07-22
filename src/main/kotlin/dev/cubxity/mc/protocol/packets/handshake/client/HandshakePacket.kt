package dev.cubxity.mc.protocol.packets.handshake.client

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * Handshake packet
 * https://wiki.vg/Protocol#Handshake
 * @author Cubxity
 * @since 7/21/2019
 */
class HandshakePacket(var protocolVersion: Int, var hostname: String, var port: Int, var intent: Intent) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        protocolVersion = buf.readVarInt()
        hostname = buf.readString()
        port = buf.readUnsignedShort()
        intent = if (buf.readVarInt() == 1) Intent.STATUS else Intent.LOGIN
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(protocolVersion)
        out.writeString(hostname)
        out.writeShort(port.toShort())
        out.writeVarInt(if (intent == Intent.STATUS) 1 else 2)
    }

    enum class Intent {
        STATUS,
        LOGIN
    }
}