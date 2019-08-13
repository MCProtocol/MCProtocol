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
import dev.cubxity.mc.protocol.data.magic.Pose
import dev.cubxity.mc.protocol.data.obj.EntityMetadata
import dev.cubxity.mc.protocol.data.obj.Rotation
import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.data.obj.VillagerData
import dev.cubxity.mc.protocol.data.obj.chunks.BlockState
import dev.cubxity.mc.protocol.data.obj.particle.*
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.exception.MalformedPacketException
import dev.cubxity.mc.protocol.net.io.stream.NetInputStream
import java.nio.charset.Charset
import java.util.*


/**
 * https://wiki.vg/Protocol#Data_types
 * @author Steveice10
 * @author Cubxity
 * @since 7/20/2019
 */
abstract class NetInput {
    abstract fun readBoolean(): Boolean
    abstract fun readByte(): Byte
    abstract fun readUnsignedByte(): Int
    abstract fun readShort(): Short
    abstract fun readUnsignedShort(): Int
    abstract fun readChar(): Char
    abstract fun readInt(): Int
    abstract fun readVarInt(): Int
    abstract fun readLong(): Long
    abstract fun readVarLong(): Long
    abstract fun readFloat(): Float
    abstract fun readDouble(): Double
    abstract fun readBytes(length: Int): ByteArray
    abstract fun readBytes(b: ByteArray): Int
    abstract fun readBytes(b: ByteArray, offset: Int, length: Int): Int
    abstract fun readShorts(length: Int): ShortArray
    abstract fun readShorts(s: ShortArray): Int
    abstract fun readShorts(s: ShortArray, offset: Int, length: Int): Int
    abstract fun readInts(length: Int): IntArray
    abstract fun readInts(i: IntArray): Int
    abstract fun readInts(i: IntArray, offset: Int, length: Int): Int
    abstract fun readLongs(length: Int): LongArray
    abstract fun readLongs(l: LongArray): Int
    abstract fun readLongs(l: LongArray, offset: Int, length: Int): Int

    fun readString(maxLength: Int = Int.MAX_VALUE): String {
        val length = this.readVarInt()

        if (length > maxLength) {
            throw MalformedPacketException("String length ($length) > maxLength ($maxLength)")
        }

        val bytes = this.readBytes(length)
        return bytes.toString(Charset.forName("UTF-8"))
    }

    fun readUUID() = UUID(this.readLong(), this.readLong())
    fun readAngle() = readByte() * 360 / 256f
    fun readVelocity() = (readShort() / 8000.0).toShort()
    fun readRotation() = Rotation(readFloat(), readFloat(), readFloat())
    fun readMessage() = Message.fromJson(readString(32767))

    fun readPosition(): SimplePosition {
        val value = readLong()
        val x = value shr 38
        val y = value and 0xFFF
        val z = value shl 26 shr 38
        return SimplePosition(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun readEntityMetadata(target: ProtocolVersion): Array<EntityMetadata> {
        var items = arrayOf<EntityMetadata>()

        while (true) {
            try {
                val id = readUnsignedByte()
                if (id == 255) break

                val typeId = readVarInt()
                val type = MetadataType.values()[typeId]

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
                    MetadataType.PARTICLE -> {
                        val particleId = readVarInt()

                        Particle(particleId, readParticleData(particleId))
                    }
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

    fun readNbt(): Tag {
        val b = readByte()
        return NBTIO.readTag(NetInputStream(this, b)) ?: CompoundTag("")
    }

    fun readSlot(): Slot {
        return if (!readBoolean()) {
            Slot(false)
        } else {
            Slot(true, readVarInt(), readByte().toInt(), readNbt())
        }
    }

    fun <T> readVarArray(lengthReader: () -> Int = { readVarInt() }, reader: () -> T): ArrayList<T> {
        val recipeIdSize = lengthReader()
        val shit: ArrayList<T> = ArrayList(recipeIdSize)

        for (index in 0 until recipeIdSize) {
            shit.add(reader())
        }

        return shit
    }

    fun readBlockState(): BlockState {
        return BlockState(readVarInt())
    }

    abstract fun available(): Int
    fun readParticleData(particleId: Int): AbstractParticleData? {
        return when (particleId) {
            3, 20 -> BlockParticleData(readVarInt())
            11 -> DustParticleData(readFloat(), readFloat(), readFloat(), readFloat())
            27 -> ItemParticleData(readSlot())
            else -> null
        }
    }

    fun <T> readOptional(function: () -> T): T? = if (readBoolean()) function() else null
}