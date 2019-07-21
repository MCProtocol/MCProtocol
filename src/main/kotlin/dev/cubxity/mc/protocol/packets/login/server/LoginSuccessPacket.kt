package dev.cubxity.mc.protocol.packets.login.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class LoginSuccessPacket(var uuid: String, var username: String) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        uuid = buf.readString()
        username = buf.readString()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(uuid)
        out.writeString(username)
    }
}