package dev.cubxity.mc.protocol.net

import com.github.steveice10.opennbt.NBTIO
import com.github.steveice10.opennbt.tag.builtin.CompoundTag
import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Direction
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.MetadataType
import dev.cubxity.mc.protocol.data.magic.Pose
import dev.cubxity.mc.protocol.data.obj.EntityMetadata
import dev.cubxity.mc.protocol.data.obj.Rotation
import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.data.obj.VillagerData
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.entities.SimplePosition
import io.netty.buffer.ByteBuf
import java.io.IOException
import java.io.InputStream
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

    fun readFloat() = buf.readFloat()
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
        val y = value and 0xFFF
        val z = value shl 26 shr 38
        return SimplePosition(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun readRotation(): Rotation = Rotation(readFloat(), readFloat(), readFloat())

    fun readEntityMetadata(target: ProtocolVersion): Array<EntityMetadata> {
        var items = arrayOf<EntityMetadata>()

        while (true) {
            try {
                val id = readUnsignedByte()
                if (id == 255) break

                val typeId = readVarInt()
                val type = MagicRegistry.lookupKey<MetadataType>(target, typeId)

                val value: Any? = when (type) {
                    MetadataType.BYTE -> readByte()
                    MetadataType.VAR_INT -> readVarInt()
                    MetadataType.FLOAT -> readFloat()
                    MetadataType.STRING -> readString()
                    MetadataType.CHAT -> Message.fromJson(readString())
                    MetadataType.OPT_CHAT -> if (readBoolean()) Message.fromJson(readString()) else null
                    MetadataType.SLOT -> readSlot()
                    MetadataType.BOOLEAN -> readBoolean()
                    MetadataType.ROTATION -> readRotation()
                    MetadataType.POSITION -> readPosition()
                    MetadataType.OPT_POSITION -> if (readBoolean()) readPosition() else null
                    MetadataType.DIRECTION -> MagicRegistry.lookupKey<Direction>(target, readVarInt())
                    MetadataType.OPT_UUID -> if (readBoolean()) readUUID() else null
                    MetadataType.OPT_BLOCK_ID -> readVarInt()
                    MetadataType.NBT -> readNbt()
                    // TODO: Add particle data
                    MetadataType.PARTICLE -> null
                    MetadataType.VILLAGER_DATA -> VillagerData(readVarInt(), readVarInt(), readVarInt())
                    MetadataType.OPT_VAR_INT -> if (readBoolean()) readVarInt() else null
                    MetadataType.POSE -> MagicRegistry.lookupKey<Pose>(target, readVarInt())
                }

                items += EntityMetadata(id, type, value)
            } catch (e: Exception) {
                break
            }
        }

        return items
    }

    fun readMessage() = Message.fromJson(readString())
    fun readNbt(): CompoundTag? {
        val b = readByte()
        return if (b.toInt() == 0) {
            null
        } else {
            NBTIO.readTag(NetInputStream(this, b)) as CompoundTag
        }
    }

    fun readSlot(): Slot {
        val present = readBoolean()

        if (!present)
            return Slot(false)

        return Slot(true, readVarInt(), readByte().toInt(), readNbt())
    }

    fun available() = buf.readableBytes()
    fun readerIndex(i: Int) = buf.readerIndex(i)
}

private class NetInputStream(private val `in`: NetInput, private val firstByte: Byte) : InputStream() {
    private var readFirst: Boolean = false

    @Throws(IOException::class)
    override fun read(): Int {
        if (!this.readFirst) {
            this.readFirst = true
            return this.firstByte.toInt()
        } else {
            return this.`in`.readUnsignedByte()
        }
    }
}
