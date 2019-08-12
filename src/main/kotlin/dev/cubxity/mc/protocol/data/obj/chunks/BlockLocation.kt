/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks

import dev.cubxity.mc.protocol.entities.SimplePosition
import java.util.*

class BlockLocation(val x: Int, val y: Int, val z: Int) {

    constructor(location: ChunkLocation) : this(location.x shl 4, location.y shl 4, location.z shl 4)

    constructor(position: SimplePosition) : this(position.x.toInt(), position.y.toInt(), position.z.toInt())

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as BlockLocation?

        return x == that!!.x &&
                y == that.y &&
                z == that.z
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }

    override fun toString(): String {
        return "BlockLocation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}'.toString()
    }
}
