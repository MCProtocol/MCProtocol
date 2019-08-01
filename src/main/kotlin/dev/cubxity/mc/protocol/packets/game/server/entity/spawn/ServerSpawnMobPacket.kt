package dev.cubxity.mc.protocol.packets.game.server.entity.spawn

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.MobType
import dev.cubxity.mc.protocol.data.obj.EntityMetadata
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerSpawnMobPacket(
    var entityId: Int,
    var entityUuid: UUID,
    var type: MobType,
    var x: Double,
    var y: Double,
    var z: Double,
    var pitch: Float,
    var yaw: Float,
    var headPitch: Float,
    var velocityX: Short,
    var velocityY: Short,
    var velocityZ: Short,
    var metadata: Array<EntityMetadata>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        entityUuid = buf.readUUID()
//        println(buf.readVarInt())
        type = MagicRegistry.lookupKey(target, buf.readVarInt())
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        pitch = buf.readAngle()
        yaw = buf.readAngle()
        headPitch = buf.readAngle()
        velocityX = buf.readVelocity()
        velocityY = buf.readVelocity()
        velocityZ = buf.readVelocity()
        metadata = buf.readEntityMetadata(target)
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeUUID(entityUuid)
        out.writeVarInt(MagicRegistry.lookupValue(target, type))
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
        out.writeAngle(pitch)
        out.writeAngle(yaw)
        out.writeAngle(headPitch)
        out.writeVelocity(velocityX)
        out.writeVelocity(velocityY)
        out.writeVelocity(velocityZ)
        out.writeEntityMetadata(metadata, target)
    }
}