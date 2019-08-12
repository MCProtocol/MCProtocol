/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.net.io

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
import dev.cubxity.mc.protocol.data.obj.chunks.BlockState
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.io.stream.NetOutputStream
import java.util.*

/**
 * https://wiki.vg/Protocol#Data_types
 * @author Steveice10
 * @author Cubxity
 * @since 7/20/2019
 */
abstract class NetOutput {
    abstract fun writeBoolean(b: Boolean): Any
    abstract fun writeByte(b: Int): Any
    abstract fun writeShort(s: Short): Any
    abstract fun writeChar(c: Int): Any
    abstract fun writeInt(i: Int): Any
    abstract fun writeVarInt(i: Int): Any
    abstract fun writeLong(l: Long): Any
    abstract fun writeVarLong(l: Long): Any
    abstract fun writeFloat(f: Float): Any
    abstract fun writeDouble(d: Double): Any
    abstract fun writeBytes(b: ByteArray): Any
    abstract fun writeBytes(b: ByteArray, length: Int): Any
    abstract fun writeShorts(s: ShortArray): Any
    abstract fun writeInts(i: IntArray): Any
    abstract fun writeLongs(l: LongArray): Any

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
    fun writeVelocity(vel: Short) = writeShort((vel * 8000).toShort())
    fun writePosition(position: SimplePosition) {
        val x = position.x.toLong()
        val y = position.y.toLong()
        val z = position.z.toLong()
        writeLong(x and 0x3FFFFFF shl 38 or (z and 0x3FFFFFF shl 12) or (y and 0xFFF))
    }

    fun writeRotation(rotation: Rotation) {
        writeFloat(rotation.pitch)
        writeFloat(rotation.yaw)
        writeFloat(rotation.roll)
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
        writeBoolean(data.isPresent)

        if (!data.isPresent)
            return

        writeVarInt(data.itemId ?: return)
        writeByte(data.count ?: return)
        writeNbt(data.nbt ?: return)
    }

    fun <T> writeVarArray(values: ArrayList<T>, writer: (T) -> Unit) {
        writeVarInt(values.size)

        for (element in values) {
            writer(element)
        }
    }

    fun writeBlockState(state: BlockState) {
        writeVarInt(state.id)
    }

}