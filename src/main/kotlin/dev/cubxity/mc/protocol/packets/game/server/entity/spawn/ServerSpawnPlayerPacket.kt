package dev.cubxity.mc.protocol.packets.game.server.entity.spawn

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.EntityMetadata
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerSpawnPlayerPacket(
    var entityId: Int,
    var playerUuid: UUID,
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float,
    var metadata: Array<EntityMetadata>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        playerUuid = buf.readUUID()
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        yaw = buf.readAngle()
        pitch = buf.readAngle()
        metadata = buf.readEntityMetadata(target)
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeUUID(playerUuid)
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
        out.writeAngle(yaw)
        out.writeAngle(pitch)
        out.writeEntityMetadata(metadata, target)
    }
}