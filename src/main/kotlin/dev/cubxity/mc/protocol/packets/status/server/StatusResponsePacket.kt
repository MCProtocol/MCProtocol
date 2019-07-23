package dev.cubxity.mc.protocol.packets.status.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.ServerListData
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class StatusResponsePacket(var data: ServerListData) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        data = ServerListData.fromJson(buf.readString())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(data.toJson())
    }
}