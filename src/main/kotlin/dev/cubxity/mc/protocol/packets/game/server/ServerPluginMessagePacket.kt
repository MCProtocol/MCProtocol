package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerPluginMessagePacket(
    var channel: String,
    var data: ByteArray
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        channel = buf.readString()
        data = buf.readBytes(buf.available())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(channel)
        out.writeBytes(data)
    }
}