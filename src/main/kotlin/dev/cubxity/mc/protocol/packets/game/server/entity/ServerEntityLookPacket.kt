package dev.cubxity.mc.protocol.packets.game.server.entity

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerEntityLookPacket(
    var entityId: Int,
    var yaw: Float,
    var pitch: Float,
    var onGround: Boolean
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        yaw = buf.readAngle()
        pitch = buf.readAngle()
        onGround = buf.readBoolean()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeAngle(yaw)
        out.writeAngle(pitch)
        out.writeBoolean(onGround)
    }
}