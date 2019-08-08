/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.*
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
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
    var viewDistance: Int,
    var reducedDebugInfo: Boolean = false
) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        this.entityId = buf.readInt()
        val gm = buf.readUnsignedByte()
        hardcore = gm and 8 == 8
        gamemode = MagicRegistry.lookupKey(target, gm and -9)
        dimension = MagicRegistry.lookupKey(target, buf.readInt())
        maxPlayers = buf.readUnsignedByte()
        levelType = MagicRegistry.lookupKey(target, buf.readString().toLowerCase())
        viewDistance = buf.readVarInt()
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
        out.writeVarInt(this.viewDistance)
        out.writeBoolean(this.reducedDebugInfo)
    }
}