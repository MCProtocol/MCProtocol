package dev.cubxity.mc.protocol.packets.game.server.state

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.MagicRegistry
import dev.cubxity.mc.protocol.data.enums.game.EnumDifficulty
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerDifficultyPacket(
    var difficulty: EnumDifficulty
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        difficulty = MagicRegistry.lookupKey(target, buf.readByte())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeByte(MagicRegistry.lookupValue(target, difficulty))
    }
}