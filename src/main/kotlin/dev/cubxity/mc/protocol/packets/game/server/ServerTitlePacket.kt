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
import dev.cubxity.mc.protocol.data.magic.TitleAction.*
import dev.cubxity.mc.protocol.data.obj.title.*
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerTitlePacket(
    var action: AbstractTitleAction
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        action = when (values()[buf.readVarInt()]) {
            SET_TITLE -> SetTitleAction(buf.readMessage())
            SET_SUBTITLE -> SetSubtitleAction(buf.readMessage())
            SET_ACTION_BAR -> SetActionBarAction(buf.readMessage())
            SET_TIMES_AND_DISPLAY -> SetTimesAndDisplayTitleAction(buf.readInt(), buf.readInt(), buf.readInt())
            HIDE -> HideTitleAction()
            RESET -> ResetTitleAction()
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        when (action) {
            is SetTitleAction -> {
                out.writeVarInt(SET_TITLE.ordinal)
                out.writeMessage((action as SetTitleAction).title)
            }
            is SetSubtitleAction -> {
                out.writeVarInt(SET_SUBTITLE.ordinal)
                out.writeMessage((action as SetSubtitleAction).title)
            }
            is SetActionBarAction -> {
                out.writeVarInt(SET_ACTION_BAR.ordinal)
                out.writeMessage((action as SetActionBarAction).title)
            }
            is SetTimesAndDisplayTitleAction -> {
                out.writeVarInt(SET_TIMES_AND_DISPLAY.ordinal)
                out.writeInt((action as SetTimesAndDisplayTitleAction).fadeIn)
                out.writeInt((action as SetTimesAndDisplayTitleAction).stay)
                out.writeInt((action as SetTimesAndDisplayTitleAction).fadeOut)
            }
            is HideTitleAction -> {
                out.writeVarInt(HIDE.ordinal)
            }
            is ResetTitleAction -> {
                out.writeVarInt(RESET.ordinal)
            }
        }
    }

}
