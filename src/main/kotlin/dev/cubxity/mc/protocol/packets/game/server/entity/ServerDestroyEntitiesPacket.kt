package dev.cubxity.mc.protocol.packets.game.server.entity

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerDestroyEntitiesPacket(
    var entities: Array<Int>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        var ids = arrayOf<Int>()

        for (i in 0 until buf.readVarInt()) {
            ids += buf.readVarInt()
        }

        entities = ids
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entities.size)
        for (e in entities) {
            out.writeVarInt(e)
        }
    }
}