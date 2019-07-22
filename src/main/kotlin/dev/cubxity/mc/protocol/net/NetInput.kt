package dev.cubxity.mc.protocol.net

import dev.cubxity.mc.protocol.entities.SimplePosition
import io.netty.buffer.ByteBuf
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 * https://wiki.vg/Protocol#Data_types
 * @author Steveice10
 * @author Cubxity
 * @since 7/20/2019
 */
class NetInput(val buf: ByteBuf) {

    fun readBoolean() = buf.readBoolean()
    fun readByte() = buf.readByte()
    fun readUnsignedByte() = buf.readUnsignedByte().toInt()
    fun readShort() = buf.readShort()
    fun readUnsignedShort() = buf.readUnsignedShort()
    fun readChar() = buf.readChar()
    fun readInt() = buf.readInt()

    fun readVarInt(): Int {
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

    fun readLong() = buf.readLong()

    fun readVarLong(): Long {
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

    fun readFloat() =buf.readFloat()
    fun readDouble() = buf.readDouble()

    fun readBytes(length: Int): ByteArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val b = ByteArray(length)
        this.buf.readBytes(b)
        return b
    }

    fun readBytes(b: ByteArray) = readBytes(b, 0, b.size)

    fun readBytes(b: ByteArray, offset: Int, length: Int): Int {
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

    fun readShorts(length: Int): ShortArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val s = ShortArray(length)
        for (index in 0 until length) {
            s[index] = this.readShort()
        }

        return s
    }

    fun readShorts(s: ShortArray) = readShorts(s, 0, s.size)

    fun readShorts(s: ShortArray, offset: Int, length: Int): Int {
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

    fun readInts(length: Int): IntArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val i = IntArray(length)
        for (index in 0 until length) {
            i[index] = this.readInt()
        }

        return i
    }

    fun readInts(i: IntArray) = readInts(i, 0, i.size)

    fun readInts(i: IntArray, offset: Int, length: Int): Int {
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

    fun readLongs(length: Int): LongArray {
        if (length < 0) {
            throw IllegalArgumentException("Array cannot have length less than 0.")
        }

        val l = LongArray(length)
        for (index in 0 until length) {
            l[index] = this.readLong()
        }

        return l
    }

    fun readLongs(l: LongArray) = readLongs(l, 0, l.size)

    fun readLongs(l: LongArray, offset: Int, length: Int): Int {
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

    fun readString(): String {
        val length = this.readVarInt()
        val bytes = this.readBytes(length)
        return bytes.toString(Charset.forName("UTF-8"))
    }

    fun readUUID() = UUID(this.readLong(), this.readLong())
    fun readAngle() = buf.readByte() * 360 / 256f
    fun readVelocity() = (buf.readShort() / 8000.0).toShort()
    fun readPosition(): SimplePosition {
        val value = readLong()
        val x = value shr 38
        val y = value shr 26 and 0xFFF
        val z = value shl 38 shr 38
        return SimplePosition(x.toDouble(), y.toDouble(), z.toDouble())
    }
    fun available() = buf.readableBytes()
    fun readerIndex(i: Int) = buf.readerIndex(i)
}