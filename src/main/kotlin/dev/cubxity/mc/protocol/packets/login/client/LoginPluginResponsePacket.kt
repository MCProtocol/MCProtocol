package dev.cubxity.mc.protocol.packets.login.client

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class LoginPluginResponsePacket(var id: Int, var success: Boolean, var data: ByteArray) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        id = buf.readVarInt()
        success = buf.readBoolean()
        data = buf.readBytes(buf.available())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(id)
        out.writeBoolean(success)
        out.writeBytes(data)
    }
}