package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Dimension
import dev.cubxity.mc.protocol.data.magic.Gamemode
import dev.cubxity.mc.protocol.data.magic.LevelType
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/22/2019
 */
class ServerJoinGamePacket @JvmOverloads constructor(
    var entityId: Int,
    var gamemode: Gamemode,
    var hardcore: Boolean,
    var dimension: Dimension,
    var maxPlayers: Int,
    var levelType: LevelType,
    var reducedDebugInfo: Boolean = false
) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        this.entityId = buf.readInt()
        val gm = buf.readUnsignedByte()
        hardcore = gm and 8 == 8
        gamemode = MagicRegistry.lookupKey(target, gm and -9)
        gamemode = MagicRegistry.lookupKey(target, gamemode)
        dimension = MagicRegistry.lookupKey(target, buf.readInt())
        maxPlayers = buf.readUnsignedByte()
        levelType = MagicRegistry.lookupKey(target, buf.readString().toLowerCase())
        reducedDebugInfo = buf.readBoolean()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeInt(this.entityId)
        var gm = MagicRegistry.lookupValue<Int>(target, gamemode)
        if (hardcore)
            gm = gm or 8
        out.writeByte(gm)
        out.writeInt(MagicRegistry.lookupValue(target, dimension))
        out.writeByte(this.maxPlayers)
        out.writeString(MagicRegistry.lookupValue(target, levelType))
        out.writeBoolean(this.reducedDebugInfo)
    }
}