package dev.cubxity.mc.protocol.packets.game.server.entity.player

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.PositionElement
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet


class ServerPlayerPositionLookPacket(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float,
    var relative: Array<PositionElement>,
    var teleportId: Int
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        yaw = buf.readFloat()
        pitch = buf.readFloat()

        val flags = buf.readUnsignedByte()
        for (element in PositionElement.values()) {
            val bit = 1 shl MagicRegistry.lookupValue(target, element)

            if (flags and bit == bit) {
                relative += element
            }
        }

        teleportId = buf.readVarInt()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
        out.writeFloat(yaw)
        out.writeFloat(pitch)

        var flags = 0
        for (element in relative) {
            flags = flags or (1 shl MagicRegistry.lookupValue(target, element))
        }

        out.writeByte(flags)
        out.writeVarInt(teleportId)
    }
}