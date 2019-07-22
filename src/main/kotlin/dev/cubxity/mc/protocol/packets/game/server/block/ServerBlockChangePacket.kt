package dev.cubxity.mc.protocol.packets.game.server.block

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerBlockChangePacket(
    var location: SimplePosition,
    var blockId: Int
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        location = buf.readPosition()
        blockId = buf.readVarInt()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writePosition(location)
        out.writeVarInt(blockId)
    }
}