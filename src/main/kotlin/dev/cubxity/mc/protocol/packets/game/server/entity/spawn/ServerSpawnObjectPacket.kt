package dev.cubxity.mc.protocol.packets.game.server.entity.spawn

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.MagicRegistry
import dev.cubxity.mc.protocol.data.enum.ObjectType
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerSpawnObjectPacket(
    var entityId: Int,
    var objectUuid: UUID,
    var type: ObjectType,
    var x: Double,
    var y: Double,
    var z: Double,
    var pitch: Float,
    var yaw: Float,
    var data: Int,
    var velocityX: Short,
    var velocityY: Short,
    var velocityZ: Short
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        objectUuid = buf.readUUID()
        type = MagicRegistry.lookupKey(target, buf.readByte()) ?: return
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        pitch = buf.readAngle()
        yaw = buf.readAngle()
        data = buf.readInt()
        velocityX = buf.readVelocity()
        velocityY = buf.readVelocity()
        velocityZ = buf.readVelocity()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeUUID(objectUuid)
        out.writeByte(MagicRegistry.lookupValue(target, type))
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
        out.writeAngle(pitch)
        out.writeAngle(yaw)
        out.writeInt(data)
        out.writeShort((velocityX * 8000.0).toShort())
        out.writeShort((velocityY * 8000.0).toShort())
        out.writeShort((velocityZ * 8000.0).toShort())
    }
}