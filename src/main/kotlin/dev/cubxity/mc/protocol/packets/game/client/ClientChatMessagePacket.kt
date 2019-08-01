package dev.cubxity.mc.protocol.packets.game.client

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ClientChatMessagePacket(
    var message: String
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        message = buf.readString()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(message)
    }
}