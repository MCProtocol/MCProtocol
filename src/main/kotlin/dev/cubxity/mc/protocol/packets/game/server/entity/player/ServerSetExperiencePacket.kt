package dev.cubxity.mc.protocol.packets.game.server.entity.player

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerSetExperiencePacket(
    var experienceBar: Float,
    var level: Int,
    var totalExperience: Int
) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        experienceBar = buf.readFloat()
        level = buf.readVarInt()
        totalExperience = buf.readVarInt()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeFloat(experienceBar)
        out.writeVarInt(level)
        out.writeVarInt(totalExperience)
    }
}