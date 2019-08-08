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

import dev.cubxity.mc.protocol.net.io.NetOutput
import io.netty.buffer.ByteBuf

/**
 * https://wiki.vg/Protocol#Data_types
 * @author Steveice10
 * @author Cubxity
 * @since 7/20/2019
 */
class ByteBufNetOutput(val buf: ByteBuf) : NetOutput() {
    override fun writeBoolean(b: Boolean): ByteBuf = buf.writeBoolean(b)
    override fun writeByte(b: Int): ByteBuf = buf.writeByte(b)
    override fun writeShort(s: Short): ByteBuf = buf.writeShort(s.toInt())
    override fun writeChar(c: Int): ByteBuf = buf.writeChar(c)
    override fun writeInt(i: Int): ByteBuf = buf.writeInt(i)

    override fun writeVarInt(i: Int) {
        var i = i
        while (i and 0x7F.inv() != 0) {
            writeByte(i and 0x7F or 0x80)
            i = i ushr 7
        }
        writeByte(i)
    }

    override fun writeLong(l: Long): ByteBuf = buf.writeLong(l)

    override fun writeVarLong(l: Long) {
        var l = l
        while (l and 0x7F.inv() != 0L) {
            writeByte((l and 0x7F).toInt() or 0x80)
            l = l ushr 7
        }

        writeByte(l.toInt())
    }

    override fun writeFloat(f: Float): ByteBuf = buf.writeFloat(f)
    override fun writeDouble(d: Double): ByteBuf = buf.writeDouble(d)
    override fun writeBytes(b: ByteArray): ByteBuf = buf.writeBytes(b)
    override fun writeBytes(b: ByteArray, length: Int): ByteBuf = buf.writeBytes(b, 0, length)
    override fun writeShorts(s: ShortArray) = s.forEach { writeShort(it) }
    override fun writeInts(i: IntArray) = i.forEach { writeInt(it) }
    override fun writeLongs(l: LongArray) = l.forEach { writeLong(it) }
}