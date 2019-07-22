package dev.cubxity.mc.protocol.packets.game.server.entity.animation

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.MagicRegistry
import dev.cubxity.mc.protocol.data.enums.EnumAnimationType
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerAnimationPacket(
    var entityId: Int,
    var animation: EnumAnimationType
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        animation = MagicRegistry.lookupKey(target, buf.readByte())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeByte(MagicRegistry.lookupValue(target, animation))
    }
}