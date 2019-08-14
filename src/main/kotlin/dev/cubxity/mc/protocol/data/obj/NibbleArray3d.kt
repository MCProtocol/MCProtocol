/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj

import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput

import java.io.IOException
import java.util.Arrays

//
class NibbleArray3d {
    var data: ByteArray? = null
        private set

    constructor(size: Int) {
        this.data = ByteArray(size shr 1)
    }

    constructor(array: ByteArray) {
        this.data = array
    }

    @Throws(IOException::class)
    constructor(`in`: NetInput, size: Int) {
        this.data = `in`.readBytes(size)
    }

    @Throws(IOException::class)
    fun write(out: NetOutput) {
        out.writeBytes(this.data!!)
    }

    operator fun get(x: Int, y: Int, z: Int): Int {
        val key = y shl 8 or (z shl 4) or x
        val index = key shr 1
        val part = key and 1
        return if (part == 0) this.data!![index].toInt() and 15 else this.data!![index].toInt() shr 4 and 15
    }

    operator fun set(x: Int, y: Int, z: Int, `val`: Int) {
        val key = y shl 8 or (z shl 4) or x
        val index = key shr 1
        val part = key and 1
        if (part == 0) {
            this.data?.set(index, (this.data!![index].toInt() and 240 or (`val` and 15)).toByte())
        } else {
            this.data?.set(index, (this.data!![index].toInt() and 15 or (`val` and 15 shl 4)).toByte())
        }
    }

    fun fill(`val`: Int) {
        for (index in 0 until (this.data!!.size shl 1)) {
            val ind = index shr 1
            val part = index and 1
            if (part == 0) {
                this.data!![ind] = (this.data!![ind].toInt() and 240 or (`val` and 15)).toByte()
            } else {
                this.data!![ind] = (this.data!![ind].toInt() and 15 or (`val` and 15 shl 4)).toByte()
            }
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is NibbleArray3d) return false

        val that = o as NibbleArray3d?
        return Arrays.equals(this.data, that!!.data)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(this.data)
    }


}