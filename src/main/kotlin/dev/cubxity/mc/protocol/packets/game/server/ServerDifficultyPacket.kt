package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Difficulity
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerDifficultyPacket(
    var difficulty: Difficulity,
    var locked: Boolean
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        difficulty = MagicRegistry.lookupKey(target, buf.readUnsignedByte())
        locked = buf.readBoolean()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeByte(MagicRegistry.lookupValue(target, difficulty))
        out.writeBoolean(locked)
    }
}