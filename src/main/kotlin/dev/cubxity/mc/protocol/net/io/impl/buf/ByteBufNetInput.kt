/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.net.io.impl.buf

import dev.cubxity.mc.protocol.net.io.NetInput
import io.netty.buffer.ByteBuf
import java.io.IOException


/**
 * https://wiki.vg/Protocol#Data_types
 * @author Steveice10
 * @author Cubxity
 * @since 7/20/2019
 */
class ByteBufNetInput(val buf: ByteBuf) : NetInput() {

    override fun readBoolean() = buf.readBoolean()
    override fun readByte() = buf.readByte()
    override fun readUnsignedByte() = buf.readUnsignedByte().toInt()
    override fun readShort() = buf.readShort()
    override fun readUnsignedShort() = buf.readUnsignedShort()
    override fun readChar() = buf.readChar()
    override fun readInt() = buf.readInt()

    override fun readVarInt(): Int {
        var value = 0
        var size = 0
        var b: Int
        while (readByte().toInt().also { b = it } and 0x80 == 0x80) {
            value = value or (b and 0x7F shl size++ * 7)
            if (size > 5) {
                throw IOException("VarInt too long (length must be <= 5)")
            }
        }

        return value or (b and 0x7F shl size * 7)
    }

    override fun readLong() = buf.readLong()

    override fun readVarLong(): Long {
        var value = 0
        var size = 0
        var b: Int
        while (readByte().toInt().also { b = it } and 0x80 == 0x80) {
            value = value or (b and 0x7F shl size++ * 7)
            if (size > 10) {
                throw IOException("VarLong too long (length must be <= 10)")
            }
        }

        return (value or (b and 0x7F shl size * 7)).toLong()
    }

    override fun readFloat() = buf.readFloat()
    override fun readDouble() = buf.readDouble()

    override fun readBytes(length: Int): ByteArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val b = ByteArray(length)
        this.buf.readBytes(b)
        return b
    }

    override fun readBytes(b: ByteArray) = readBytes(b, 0, b.size)

    override fun readBytes(b: ByteArray, offset: Int, length: Int): Int {
        var length = length
        val readable = this.buf.readableBytes()
        if (readable <= 0) {
            return -1
        }

        if (readable < length) {
            length = readable
        }

        this.buf.readBytes(b, offset, length)
        return length
    }

    override fun readShorts(length: Int): ShortArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val s = ShortArray(length)
        for (index in 0 until length) {
            s[index] = this.readShort()
        }

        return s
    }

    override fun readShorts(s: ShortArray) = readShorts(s, 0, s.size)

    override fun readShorts(s: ShortArray, offset: Int, length: Int): Int {
        var length = length
        val readable = this.buf.readableBytes()
        if (readable <= 0) {
            return -1
        }

        if (readable < length * 2) {
            length = readable / 2
        }

        for (index in offset until offset + length) {
            s[index] = this.readShort()
        }

        return length
    }

    override fun readInts(length: Int): IntArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val i = IntArray(length)
        for (index in 0 until length) {
            i[index] = this.readInt()
        }

        return i
    }

    override fun readInts(i: IntArray) = readInts(i, 0, i.size)

    override fun readInts(i: IntArray, offset: Int, length: Int): Int {
        var length = length
        val readable = this.buf.readableBytes()
        if (readable <= 0) {
            return -1
        }

        if (readable < length * 4) {
            length = readable / 4
        }

        for (index in offset until offset + length) {
            i[index] = this.readInt()
        }

        return length
    }

    override fun readLongs(length: Int): LongArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val l = LongArray(length)
        for (index in 0 until length) {
            l[index] = this.readLong()
        }

        return l
    }

    override fun readLongs(l: LongArray) = readLongs(l, 0, l.size)

    override fun readLongs(l: LongArray, offset: Int, length: Int): Int {
        var length = length
        val readable = this.buf.readableBytes()
        if (readable <= 0) {
            return -1
        }

        if (readable < length * 2) {
            length = readable / 2
        }

        for (index in offset until offset + length) {
            l[index] = this.readLong()
        }

        return length
    }

    override fun available() = buf.readableBytes()

    fun readerIndex(readerIndex: Int): ByteBuf = buf.readerIndex(readerIndex)
}