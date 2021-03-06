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
import dev.cubxity.mc.protocol.data.obj.bossbar.BossBarColor
import dev.cubxity.mc.protocol.data.obj.bossbar.BossBarDivision
import dev.cubxity.mc.protocol.data.obj.bossbar.actions.*
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerBossBarPacket : Packet {
    var uuid: UUID? = null
    lateinit var action: BossBarAction

    constructor(uuid: UUID, action: BossBarAction) {
        this.uuid = uuid
        this.action = action
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        uuid = buf.readUUID()

        when (buf.readVarInt()) {
            0 -> action = BossBarAddAction(
                buf.readMessage(),
                buf.readFloat(),
                BossBarColor.values()[buf.readVarInt()],
                BossBarDivision.values()[buf.readVarInt()],
                buf.readUnsignedByte()
            )
            1 -> action = BossBarRemoveAction()
            2 -> action = BossBarUpdateHealthAction(buf.readFloat())
            3 -> action =
                BossBarUpdateTitleAction(buf.readMessage())
            4 -> action = BossBarUpdateStyleAction(
                BossBarColor.values()[buf.readVarInt()],
                BossBarDivision.values()[buf.readVarInt()]
            )
            5 -> action =
                BossBarUpdateFlagsAction(buf.readUnsignedByte())
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeUUID(uuid!!)

        when (action) {
            is BossBarAddAction -> {
                out.writeVarInt(0)

                val action = action as BossBarAddAction

                out.writeMessage(action.title)
                out.writeFloat(action.health)
                out.writeVarInt(action.color.ordinal)
                out.writeVarInt(action.division.ordinal)
                out.writeByte(action.flags)
            }
            is BossBarRemoveAction -> out.writeVarInt(1)
            is BossBarUpdateHealthAction -> {
                out.writeVarInt(2)

                out.writeFloat((action as BossBarUpdateHealthAction).health)
            }
            is BossBarUpdateTitleAction -> {
                out.writeVarInt(3)

                out.writeMessage((action as BossBarUpdateTitleAction).title)
            }
            is BossBarUpdateStyleAction -> {
                out.writeVarInt(4)

                out.writeVarInt((action as BossBarUpdateStyleAction).color.ordinal)
                out.writeVarInt((action as BossBarUpdateStyleAction).division.ordinal)
            }
            is BossBarUpdateFlagsAction -> {
                out.writeVarInt(5)

                out.writeByte((action as BossBarUpdateFlagsAction).flags)
            }
        }
    }
}
