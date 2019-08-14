/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks.util

import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Gamemode
import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.data.obj.chunks.BlockState
import dev.cubxity.mc.protocol.data.registries.impl.BlockRegistry

object BlockUtil {

    fun getGlobalPaletteIDFromState(state: BlockState) = state.id
    fun getStateFromGlobalPaletteID(value: Int, target: ProtocolVersion) =
        BlockState(value)

    fun getDigTime(bot: Bot, block: BlockRegistry.BlockEntry): Double {
        if (bot.player.worldManager.gamemode == Gamemode.CREATIVE) return 0.0
        var time = 1000 * block.hardness * 1.5

        if (!canHarvest(bot.player.inventoryManager.heldItem)) { return time * 10 / 3 }
//        const toolMultiplier = toolMultipliers[this.material]
//        if (toolMultiplier && heldItemType) {
//            const multiplier = toolMultiplier[heldItemType]
//            if (multiplier) time /= multiplier
//        }

        if (!bot.player.physicsManager.onGround) time *= 5
//        if (inWater) time *= 5

        return time
    }

    private fun canHarvest(heldItemType: Slot?) = false

}