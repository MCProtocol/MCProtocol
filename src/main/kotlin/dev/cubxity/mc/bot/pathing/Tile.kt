/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.bot.pathing

import dev.cubxity.mc.protocol.entities.BlockPosition
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.utils.Vec3d
import kotlin.math.abs


data class Tile(val position: BlockPosition, var parent: Tile?) {

    var g = -1.0
    var h = -1.0

    fun calculateBoth(start: BlockPosition, end: BlockPosition, update: Boolean) {
        calculateG(update)
        calculateH(start, end, update)
    }

    private fun calculateH(start: BlockPosition, end: BlockPosition, update: Boolean) {
        if (!update && h == -1.0 || update) {
            val hx = start.x + position.x
            val hy = start.y + position.y
            val hz = start.z + position.z

            h = Vec3d(hx.toDouble(), hy.toDouble(), hz.toDouble()).distanceTo(end.toSimple().toVec3())
        }
    }

    private fun calculateG(update: Boolean) {
        if (!update && g == -1.0 || update) {
            var currentTile = this
            val currentParent = currentTile.parent
            var gCost = 0.0

            while (currentParent != null) {
                var dx = currentTile.position.x - currentParent.position.x
                var dy = currentTile.position.y - currentParent.position.y
                var dz = currentTile.position.z - currentParent.position.z

                dx = abs(dx)
                dy = abs(dy)
                dz = abs(dz)

                gCost += if (dx == 1 && dy == 1 && dz == 1) {
                    1.7
                } else if ((dx == 1 || dz == 1) && dy == 1 || (dx == 1 || dz == 1) && dy == 0) {
                    1.4
                } else {
                    1.0
                }

                currentTile = currentParent
            }

            g = gCost
        }
    }

    fun isInRange(range: Int) =
        ((range - abs(position.x) >= 0) && (range - abs(position.y) >= 0) && (range - abs(position.z) >= 0))

    fun getRealPosition(start: BlockPosition) = SimplePosition(
        position.x + start.x.toDouble(),
        position.y + start.y.toDouble(),
        position.z + start.z.toDouble()
    )

}