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
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ClientRecipeBookDataPacket(
    var displayedRecipe: String? = null,
    var craftingRecipeBookOpen: Boolean? = null,
    var craftingRecipeFilterActive: Boolean? = null,
    var smeltingRecipeBookOpen: Boolean? = null,
    var smeltingRecipeFilterActive: Boolean? = null,
    var blastingRecipeBookOpen: Boolean? = null,
    var blastingRecipeFilterActive: Boolean? = null,
    var smokingRecipeBookOpen: Boolean? = null,
    var smokingRecipeFilterActive: Boolean? = null
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        if (buf.readVarInt() == 0) {
            displayedRecipe = buf.readString()
        } else {
            craftingRecipeBookOpen = buf.readBoolean()
            craftingRecipeFilterActive = buf.readBoolean()
            smeltingRecipeBookOpen = buf.readBoolean()
            smeltingRecipeFilterActive = buf.readBoolean()
            blastingRecipeBookOpen = buf.readBoolean()
            blastingRecipeFilterActive = buf.readBoolean()
            smokingRecipeBookOpen = buf.readBoolean()
            smokingRecipeFilterActive = buf.readBoolean()
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        if (displayedRecipe != null) {
            out.writeVarInt(0)
            out.writeString(displayedRecipe!!)
        } else {
            out.writeVarInt(1)
            out.writeBoolean(craftingRecipeBookOpen!!)
            out.writeBoolean(craftingRecipeFilterActive!!)
            out.writeBoolean(smeltingRecipeBookOpen!!)
            out.writeBoolean(smeltingRecipeFilterActive!!)
            out.writeBoolean(blastingRecipeBookOpen!!)
            out.writeBoolean(blastingRecipeFilterActive!!)
            out.writeBoolean(smokingRecipeBookOpen!!)
            out.writeBoolean(smokingRecipeFilterActive!!)
        }
    }

}
