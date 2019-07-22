package dev.cubxity.mc.protocol.packets.game.server.entity.animation

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerBlockBreakAnimationPacket(
    var entityId: Int,
    var location: SimplePosition,
    var destroyStage: Byte
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        location = buf.readPosition()
        destroyStage = buf.readByte()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writePosition(location)
        out.writeByte(destroyStage.toInt())
    }
}