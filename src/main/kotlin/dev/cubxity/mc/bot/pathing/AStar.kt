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

import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.protocol.entities.BlockPosition
import java.util.*
import kotlin.math.abs


class AStar(val bot: Bot, private val start: BlockPosition, val end: BlockPosition, val range: Int) {

    private val open = hashMapOf<BlockPosition, Tile>()
    private val closed = hashMapOf<BlockPosition, Tile>()

    private var checkOnce = false
    private var endId: BlockPosition

    var result = PathingResult.SUCCESS
        private set

    init {
        val t = Tile(BlockPosition(0, 0, 0), null)
        t.calculateBoth(start, end, true)

        open[t.position] = t
        processAdjacentTiles(t)

        endId = BlockPosition(end.x - start.x, end.y - start.y, end.z - start.z)
    }

    fun iterate(): ArrayList<Tile>? {
        if (!checkOnce) {
            checkOnce = checkOnce xor true
            if (abs(start.x - end.x) > range || abs(start.y - end.y) > range || abs(start.z - end.z) > range) {
                return null
            }
        }

        var current: Tile? = null

        while (canContinue()) {
            current = getLowestFTile()
            processAdjacentTiles(current)
        }

        if (result != PathingResult.SUCCESS) {
            return null
        } else {
            val routeTrace = LinkedList<Tile>()
            var parent: Tile? = current!!.parent

            routeTrace.add(current)

            while (parent != null) {
                routeTrace.add(parent)
                current = parent
                parent = current.parent
            }

            routeTrace.reverse()

            return ArrayList(routeTrace)
        }
    }

    private fun canContinue() = if (open.size == 0) {
        result = PathingResult.NO_PATH
        false
    } else {
        if (closed.containsKey(endId)) {
            result = PathingResult.SUCCESS
            false
        } else {
            true
        }
    }

    private fun getLowestFTile(): Tile {
        var f = 0.0
        var drop: Tile? = null

        for (t in open.values) {
            if (f == 0.0) {
                t.calculateBoth(start, end, true)
                f = t.h + t.g
                drop = t
            } else {
                t.calculateBoth(start, end, true)
                val posF = t.h + t.g
                if (posF < f) {
                    f = posF
                    drop = t
                }
            }
        }

        open.remove(drop!!.position)
        closed[drop.position] = drop

        return drop
    }

    private fun processAdjacentTiles(current: Tile) {
        val possible = arrayListOf<Tile>()

        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    val t = Tile(
                        BlockPosition(current.position.x + x, current.position.y + y, current.position.z + z),
                        current
                    )

                    if (!t.isInRange(range))
                        continue

                    if (x != 0 && z != 0 && (y == 0 || y == 1)) {
                        val xOff = Tile(
                            BlockPosition(current.position.x + x, current.position.y + y, current.position.z),
                            current
                        )
                        val zOff = Tile(
                            BlockPosition(current.position.x, current.position.y + y, current.position.z + z),
                            current
                        )

                        if (!isTileWalkable(xOff) && !isTileWalkable(zOff))
                            continue
                    }

                    if (closed.containsKey(t.position))
                        continue

                    if (isTileWalkable(t)) {
                        t.calculateBoth(start, end, true)
                        possible.add(t)
                    }
                }
            }
        }

        for (t in possible) {
            val openRef = open[t.position]
            if (openRef == null) {
                open[t.position] = t
            } else {
                if (t.g < openRef.g) {
                    openRef.parent = current
                    openRef.calculateBoth(start, end, true)
                }
            }
        }
    }

    private fun isTileWalkable(t: Tile) = bot.world.getBlockAt(t.getRealPosition(start).toBlockPosition())?.id ?: 0 == 0


}