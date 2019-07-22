package dev.cubxity.mc.protocol.packets.game.server.entity.spawn

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.data.MagicRegistry
import dev.cubxity.mc.protocol.packets.data.enum.EnumGlobalEntityType
import dev.cubxity.mc.protocol.packets.data.enum.EnumObjectType

class SpawnGlobalEntityPacket(
    var entityId: Int,
    var type: EnumGlobalEntityType,
    var x: Double,
    var y: Double,
    var z: Double
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        type = MagicRegistry.lookupKey(target, buf.readByte())
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeByte(MagicRegistry.lookupValue(target, type))
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
    }
}