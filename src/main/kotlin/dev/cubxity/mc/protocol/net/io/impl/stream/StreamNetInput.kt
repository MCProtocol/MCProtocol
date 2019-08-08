/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.net.io.impl.stream

import dev.cubxity.mc.protocol.net.io.NetInput
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.lang.Double.longBitsToDouble
import java.lang.Float.intBitsToFloat


class StreamNetInput(val `in`: InputStream) : NetInput() {

    override fun readBoolean(): Boolean {
        return readByte().toInt() == 1
    }

    override fun readByte(): Byte {
        return readUnsignedByte().toByte()
    }

    override fun readUnsignedByte(): Int {
        val b = `in`.read()
        if (b < 0) {
            throw EOFException()
        }

        return b
    }

    override fun readShort(): Short {
        return readUnsignedShort().toShort()
    }

    override fun readUnsignedShort(): Int {
        val ch1 = readUnsignedByte()
        val ch2 = readUnsignedByte()
        return (ch1 shl 8) + (ch2 shl 0)
    }

    override fun readChar(): Char {
        val ch1 = readUnsignedByte()
        val ch2 = readUnsignedByte()
        return ((ch1 shl 8) + (ch2 shl 0)).toChar()
    }

    override fun readInt(): Int {
        val ch1 = readUnsignedByte()
        val ch2 = readUnsignedByte()
        val ch3 = readUnsignedByte()
        val ch4 = readUnsignedByte()
        return (ch1 shl 24) + (ch2 shl 16) + (ch3 shl 8) + (ch4 shl 0)
    }

    override fun readVarInt(): Int {
        var value = 0
        var size = 0
        var b: Int = readByte().toInt()
        while (b and 0x80 == 0x80) {
            value = value or (b and 0x7F shl size++ * 7)
            if (size > 5) {
                throw IOException("VarInt too long (length must be <= 5)")
            }
            b = readByte().toInt()
        }

        return value or (b and 0x7F shl size * 7)
    }

    override fun readLong(): Long {
        val read = readBytes(8)
        return (read[0].toLong() shl 56) + ((read[1].toLong() and 255) shl 48) + ((read[2].toLong() and 255) shl 40) + ((read[3].toLong() and 255) shl 32) + ((read[4].toLong() and 255) shl 24) + (read[5].toLong() and 255 shl 16) + (read[6].toLong() and 255 shl 8) + (read[7].toLong() and 255 shl 0)
    }

    override fun readVarLong(): Long {
        var value: Long = 0
        var size = 0
        var b: Int = readByte().toInt()
        while (b and 0x80 == 0x80) {
            value = value or ((b and 0x7F).toLong() shl size++ * 7)
            if (size > 10) {
                throw IOException("VarLong too long (length must be <= 10)")
            }

            b = readByte().toInt()
        }

        return value or ((b and 0x7F).toLong() shl size * 7)
    }

    override fun readFloat(): Float = intBitsToFloat(readInt())
    override fun readDouble(): Double = longBitsToDouble(readLong())

    override fun readBytes(length: Int): ByteArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val b = ByteArray(length)
        var n = 0
        while (n < length) {
            val count = `in`.read(b, n, length - n)
            if (count < 0) {
                throw EOFException()
            }

            n += count
        }

        return b
    }

    override fun readBytes(b: ByteArray): Int = `in`.read(b)
    override fun readBytes(b: ByteArray, offset: Int, length: Int): Int = `in`.read(b, offset, length)

    override fun readShorts(length: Int): ShortArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val s = ShortArray(length)
        val read = readShorts(s)
        if (read < length) {
            throw EOFException()
        }

        return s
    }

    override fun readShorts(s: ShortArray): Int = readShorts(s, 0, s.size)

    override fun readShorts(s: ShortArray, offset: Int, length: Int): Int {
        for (index in offset until offset + length) {
            try {
                s[index] = readShort()
            } catch (e: EOFException) {
                return index - offset
            }

        }

        return length
    }

    override fun readInts(length: Int): IntArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val i = IntArray(length)
        val read = readInts(i)
        if (read < length) {
            throw EOFException()
        }

        return i
    }

    override fun readInts(i: IntArray): Int = readInts(i, 0, i.size)

    override fun readInts(i: IntArray, offset: Int, length: Int): Int {
        for (index in offset until offset + length) {
            try {
                i[index] = readInt()
            } catch (e: EOFException) {
                return index - offset
            }

        }

        return length
    }

    override fun readLongs(length: Int): LongArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val l = LongArray(length)
        val read = readLongs(l)
        if (read < length) {
            throw EOFException()
        }

        return l
    }

    override fun readLongs(l: LongArray): Int = readLongs(l, 0, l.size)

    override fun readLongs(l: LongArray, offset: Int, length: Int): Int {
        for (index in offset until offset + length) {
            try {
                l[index] = readLong()
            } catch (e: EOFException) {
                return index - offset
            }

        }

        return length
    }

    override fun available(): Int = `in`.available()

}