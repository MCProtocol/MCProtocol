package dev.cubxity.mc.protocol.packets.game.server.entity

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerEntityVelocityPacket(
    var entityId: Int,
    var velocityX: Short,
    var velocityY: Short,
    var velocityZ: Short
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        velocityX = buf.readShort()
        velocityY = buf.readShort()
        velocityZ = buf.readShort()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeShort(velocityX)
        out.writeShort(velocityY)
        out.writeShort(velocityZ)
    }
}