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
import dev.cubxity.mc.protocol.data.magic.UnlockRecipeAction
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerUnlockRecipesPacket(
    var action: UnlockRecipeAction,
    var craftingRecipeBookOpen: Boolean,
    var craftingRecipeBookFilterActive: Boolean,
    var smeltingRecipeBookOpen: Boolean,
    var smeltingRecipeBookFilterActive: Boolean,
    var recipeIds: ArrayList<String>,
    var initRecipeIds: ArrayList<String>?
) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        action = UnlockRecipeAction.values()[buf.readVarInt()]

        craftingRecipeBookOpen = buf.readBoolean()
        craftingRecipeBookFilterActive = buf.readBoolean()
        smeltingRecipeBookOpen = buf.readBoolean()
        smeltingRecipeBookFilterActive = buf.readBoolean()

        recipeIds = buf.readVarArray { buf.readString() }


        if (action == UnlockRecipeAction.INIT) {
            initRecipeIds = buf.readVarArray { buf.readString() }
        }

    }


    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(action.ordinal)

        out.writeBoolean(craftingRecipeBookOpen)
        out.writeBoolean(craftingRecipeBookFilterActive)
        out.writeBoolean(smeltingRecipeBookOpen)
        out.writeBoolean(smeltingRecipeBookFilterActive)

        out.writeVarArray(recipeIds) { v -> out.writeString(v) }

        if (action == UnlockRecipeAction.INIT) {
            out.writeVarArray(initRecipeIds!!) { v -> out.writeString(v) }
        }
    }
}
