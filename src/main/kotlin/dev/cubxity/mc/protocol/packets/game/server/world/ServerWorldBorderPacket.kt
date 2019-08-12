/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.world


import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.worldborder.*
import dev.cubxity.mc.protocol.data.obj.worldborder.WorldBorderAction.*
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerWorldBorderPacket(
    var action: AbstractWorldBorderAction
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        val worldBorderAction = values()[buf.readVarInt()]

        action = when (worldBorderAction) {
            SET_SIZE -> SetSizeWorldBorderAction(buf.readDouble())
            LERP_SIZE -> LerpSizeWorldBorderAction(buf.readDouble(), buf.readDouble(), buf.readVarLong())
            SET_CENTER -> SetCenterWorldBorderAction(buf.readDouble(), buf.readDouble())
            INITIALIZE -> InitializeWorldBorderAction(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readVarLong(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt()
            )
            SET_WARNING_TIME -> SetWarningTimeWorldBorderAction(buf.readVarInt())
            SET_WARNING_BLOCKS -> SetWarningBlocksWorldBorderAction(buf.readVarInt())
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        when (action) {
            is SetSizeWorldBorderAction -> {
                out.writeVarInt(SET_SIZE.ordinal)
                out.writeDouble((action as SetSizeWorldBorderAction).diameter)
            }
            is LerpSizeWorldBorderAction -> {
                out.writeVarInt(LERP_SIZE.ordinal)
                out.writeDouble((action as LerpSizeWorldBorderAction).oldDiameter)
                out.writeDouble((action as LerpSizeWorldBorderAction).newDiameter)
                out.writeVarLong((action as LerpSizeWorldBorderAction).speed)
            }
            is SetCenterWorldBorderAction -> {
                out.writeVarInt(SET_CENTER.ordinal)
                out.writeDouble((action as SetCenterWorldBorderAction).x)
                out.writeDouble((action as SetCenterWorldBorderAction).z)
            }
            is InitializeWorldBorderAction -> {
                out.writeVarInt(INITIALIZE.ordinal)
                out.writeDouble((action as InitializeWorldBorderAction).x)
                out.writeDouble((action as InitializeWorldBorderAction).z)
                out.writeDouble((action as InitializeWorldBorderAction).oldDiameter)
                out.writeDouble((action as InitializeWorldBorderAction).newDiameter)
                out.writeVarLong((action as InitializeWorldBorderAction).speed)
                out.writeVarInt((action as InitializeWorldBorderAction).portalTeleportBoundary)
                out.writeVarInt((action as InitializeWorldBorderAction).warningTime)
                out.writeVarInt((action as InitializeWorldBorderAction).warningBlocks)
            }
            is SetWarningTimeWorldBorderAction -> {
                out.writeVarInt(SET_WARNING_TIME.ordinal)
                out.writeVarInt((action as SetWarningTimeWorldBorderAction).warningTime)
            }
            is SetWarningBlocksWorldBorderAction -> {
                out.writeVarInt(SET_WARNING_BLOCKS.ordinal)
                out.writeVarInt((action as SetWarningBlocksWorldBorderAction).warningBlocks)
            }

        }
    }

}
