/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.net

import com.github.steveice10.opennbt.NBTIO
import com.github.steveice10.opennbt.tag.builtin.CompoundTag
import com.github.steveice10.opennbt.tag.builtin.Tag
import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Direction
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.MetadataType
import dev.cubxity.mc.protocol.data.obj.EntityMetadata
import dev.cubxity.mc.protocol.data.obj.Rotation
import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.entities.SimplePosition
import io.netty.buffer.ByteBuf
import java.io.IOException
import java.io.OutputStream
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
        buf.writeLong(x and 0x3FFFFFF shl 38 or (z and 0x3FFFFFF shl 12) or (y and 0xFFF))
    }

    fun writeRotation(rotation: Rotation) {
        buf.writeFloat(rotation.pitch)
        buf.writeFloat(rotation.yaw)
        buf.writeFloat(rotation.roll)
    }

    fun writeEntityMetadata(data: Array<EntityMetadata>, target: ProtocolVersion) {
        for (item in data) {
            writeByte(item.id)
            writeVarInt(MagicRegistry.lookupValue(target, item.type))

            when (item.type) {
                MetadataType.BYTE -> writeByte(item.value as Int)
                MetadataType.VAR_INT -> writeVarInt(item.value as Int)
                MetadataType.FLOAT -> writeFloat(item.value as Float)
                MetadataType.STRING -> writeString(item.value as String)
                MetadataType.CHAT -> writeString((item.value as Message).toJson())
                MetadataType.OPT_CHAT -> writeString((item.value as Message).toJson())
                MetadataType.BOOLEAN -> writeBoolean(item.value as Boolean)
                MetadataType.ROTATION -> writeRotation(item.value as Rotation)
                MetadataType.POSITION -> writePosition(item.value as SimplePosition)
                MetadataType.OPT_POSITION -> writePosition(item.value as SimplePosition)
                MetadataType.DIRECTION -> writeVarInt(MagicRegistry.lookupValue(target, item.value as Direction))
                MetadataType.OPT_UUID -> writeUUID(item.value as UUID)
                MetadataType.OPT_BLOCK_ID -> writeVarInt(item.value as Int)
                MetadataType.NBT -> NBTIO.writeTag(NetOutputStream(this), item.value as CompoundTag)
                // TODO: Add particle & slot data
                else -> throw NullPointerException("No value to write: ${item.id}")
            }
        }

        writeByte(255)
    }

    fun writeMessage(m: Message) = writeString(m.toJson())
    fun writeNbt(nbt: Tag) = NBTIO.writeTag(NetOutputStream(this), nbt)
    fun writeSlot(data: Slot) {
        writeBoolean(data.present)

        if (!data.present)
            return

        writeVarInt(data.itemId ?: return)
        writeByte(data.itemCount ?: return)
        writeNbt(data.nbt ?: return)

    fun writeNBTTag(nbtData: Tag) {
        try {
            NBTIO.writeTag(NetOutputStream(this), nbtData)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

class NetOutputStream(val output: NetOutput) : OutputStream() {

    override fun write(b: Int) {
        output.writeByte(b)
    }
}