package dev.cubxity.mc.protocol.net

import dev.cubxity.mc.protocol.entities.SimplePosition
import io.netty.buffer.ByteBuf
import java.util.*

/**
 * https://wiki.vg/Protocol#Data_types
 * @author Steveice10
 * @author Cubxity
 * @since 7/20/2019
 */
class NetOutput(val buf: ByteBuf) {
    fun writeBoolean(b: Boolean) = buf.writeBoolean(b)
    fun writeByte(b: Int) = buf.writeByte(b)
    fun writeShort(s: Short) = buf.writeShort(s.toInt())
    fun writeChar(c: Int) = buf.writeChar(c)
    fun writeInt(i: Int) = buf.writeInt(i)

    fun writeVarInt(i: Int) {
        var i = i
        while (i and 0x7F.inv() != 0) {
            writeByte(i and 0x7F or 0x80)
            i = i ushr 7
        }
        writeByte(i)
    }

    fun writeLong(l: Long) = buf.writeLong(l)

    fun writeVarLong(l: Long) {
        var l = l
        while (l and 0x7F.inv() != 0L) {
            writeByte((l and 0x7F).toInt() or 0x80)
            l = l ushr 7
        }

        writeByte(l.toInt())
    }

    fun writeFloat(f: Float) = buf.writeFloat(f)
    fun writeDouble(d: Double) = buf.writeDouble(d)
    fun writeBytes(b: ByteArray) = buf.writeBytes(b)
    fun writeBytes(b: ByteArray, length: Int) = buf.writeBytes(b, 0, length)
    fun writeShorts(s: ShortArray) = s.forEach { writeShort(it) }
    fun writeInts(i: IntArray) = i.forEach { writeInt(it) }
    fun writeLongs(l: LongArray) = l.forEach { writeLong(it) }

    fun writeString(s: String) {
        val bytes = s.toByteArray(charset("UTF-8"))
        if (bytes.size > 32767) {
            throw IllegalArgumentException("String length exceeds maximum length (32767)")
        } else {
            writeVarInt(bytes.size)
            writeBytes(bytes)
        }
    }

    fun writeUUID(uuid: UUID) {
        writeLong(uuid.mostSignificantBits)
        writeLong(uuid.leastSignificantBits)
    }

    fun writeAngle(angle: Float) = writeByte((angle * 256.0f / 360.0f).toInt())
    fun writeVelocity(vel: Short) = buf.writeShort(vel * 8000)
    fun writePosition(position: SimplePosition) {
        val x = position.x.toLong()
        val y = position.y.toLong()
        val z = position.z.toLong()
        buf.writeLong(x and 0x3FFFFFF shl 38 or (y and 0xFFF shl 26) or (z and 0x3FFFFFF))
    }
}