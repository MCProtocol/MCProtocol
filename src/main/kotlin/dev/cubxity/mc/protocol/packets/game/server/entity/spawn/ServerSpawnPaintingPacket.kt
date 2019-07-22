package dev.cubxity.mc.protocol.packets.game.server.entity.spawn

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.MagicRegistry
import dev.cubxity.mc.protocol.data.enum.EnumDirection
import dev.cubxity.mc.protocol.data.enum.EnumPaintingType
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerSpawnPaintingPacket(
    var entityId: Int,
    var entityUuid: UUID,
    var motive: EnumPaintingType,
    var location: SimplePosition,
    var direction: EnumDirection
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        entityUuid = buf.readUUID()
        motive = MagicRegistry.lookupKey(target, buf.readVarInt())
        location = buf.readPosition()
        direction = MagicRegistry.lookupKey(target, buf.readByte())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeUUID(entityUuid)
        out.writeVarInt(MagicRegistry.lookupValue(target, motive))
        out.writePosition(location)
        out.writeByte(MagicRegistry.lookupValue(target, direction))
    }
}