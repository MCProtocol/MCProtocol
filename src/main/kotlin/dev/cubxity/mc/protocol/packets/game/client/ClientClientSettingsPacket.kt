/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.client


import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.ChatMode
import dev.cubxity.mc.protocol.data.magic.EnumHand
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ClientClientSettingsPacket(
    var locale: String,
    var viewDistance: Int,
    var chateMode: ChatMode,
    var chatColors: Boolean,
    var displayedSkinParts: Int,
    var mainHand: EnumHand
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        locale = buf.readString(16)
        viewDistance = buf.readUnsignedByte()
        chateMode = ChatMode.values()[buf.readVarInt()]
        chatColors = buf.readBoolean()
        displayedSkinParts = buf.readUnsignedByte()
        mainHand = EnumHand.values()[buf.readVarInt()]
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(locale)
        out.writeByte(viewDistance)
        out.writeVarInt(chateMode.ordinal)
        out.writeBoolean(chatColors)
        out.writeByte(displayedSkinParts)
        out.writeVarInt(mainHand.ordinal)
    }

}
