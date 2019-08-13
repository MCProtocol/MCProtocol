/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.utils

import kotlin.math.ceil
import kotlin.math.floor

class Vec3d(var x: Double, var y: Double, var z: Double) {

    fun add(other: Vec3d): Vec3d {
        x += other.x
        y += other.y
        z += other.z

        return this
    }

    fun scaled(scale: Double): Vec3d {
        x *= scale
        y *= scale
        z *= scale

        return this
    }

    fun floor(): Vec3d {
        x = floor(x)
        y = floor(y)
        z = floor(z)

        return this
    }

    fun ceil(): Vec3d {
        x = ceil(x)
        y = ceil(y)
        z = ceil(z)

        return this
    }

    override fun toString(): String {
        return "Vec3d{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}'.toString()
    }

}
