package dev.cubxity.mc.protocol.packets.login.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class LoginPluginRequestPacket(var id: Int, var channel: String, var data: ByteArray) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        id = buf.readVarInt()
        channel = buf.readString()
        data = buf.readBytes(buf.available())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(id)
        out.writeString(channel)
        out.writeBytes(data)
    }
}