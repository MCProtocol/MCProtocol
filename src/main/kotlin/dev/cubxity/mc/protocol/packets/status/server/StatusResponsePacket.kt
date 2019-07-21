package dev.cubxity.mc.protocol.packets.status.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class StatusResponsePacket(var json: String) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        json = buf.readString() // TODO: Serialize to an object
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(json)
    }
}