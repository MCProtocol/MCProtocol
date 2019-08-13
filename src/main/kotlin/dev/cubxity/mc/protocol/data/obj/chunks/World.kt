/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Difficulity
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerDifficultyPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerUnloadChunkPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.*
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnMobPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnPlayerPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerChunkDataPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerTimeUpdatePacket
import dev.cubxity.mc.protocol.state.entity.WorldEntity
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

class World(session: ProtocolSession) {
    private val logger = LoggerFactory.getLogger("World")
    val chunks = ConcurrentHashMap<ChunkLocation, ChunkSection>()
    var dimension: Dimension? = null
    var height: Int = 0
    var timeOfDay: Long = 0
    var worldAge: Long = 0
    var difficulty = Difficulity.NORMAL
    var difficultyLocked = false
    val entities = hashMapOf<Int, WorldEntity>()

    init {
        with(session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerChunkDataPacket }
                .map { it.packet as ServerChunkDataPacket }
                .subscribe {
                    loadChunk(it.column)
                    // TODO Implement the rest
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerUnloadChunkPacket }
                .map { it.packet as ServerUnloadChunkPacket }
                .subscribe { unloadChunkColumn(it.chunkX, it.chunkZ) }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerTimeUpdatePacket }
                .map { it.packet as ServerTimeUpdatePacket }
                .subscribe {
                    timeOfDay = it.timeOfDay
                    worldAge = it.worldAge
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerDifficultyPacket }
                .map { it.packet as ServerDifficultyPacket }
                .subscribe {
                    difficulty = it.difficulty
                    difficultyLocked = it.difficultyLocked
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerSpawnPlayerPacket }
                .map { it.packet as ServerSpawnPlayerPacket }
                .subscribe {
                    entities[it.entityId] = dev.cubxity.mc.protocol.state.entity.impl.WorldPlayerEntity(
                        it.entityId,
                        it.x,
                        it.y,
                        it.z,
                        false,
                        0f,
                        it.pitch,
                        it.yaw,
                        it.playerUuid
                    )
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerSpawnMobPacket }
                .map { it.packet as ServerSpawnMobPacket }
                .subscribe {
                    entities[it.entityId] = WorldEntity(
                        it.type,
                        it.entityId,
                        it.x,
                        it.y,
                        it.z,
                        false,
                        0f,
                        it.pitch,
                        it.yaw
                    )
                    println("${it.entityId} -> ${entities[it.entityId]}")
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityHeadLookPacket }
                .map { it.packet as ServerEntityHeadLookPacket }
                .subscribe {
                    entities[it.entityId]?.headYaw = it.headYaw
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityLookPacket }
                .map { it.packet as ServerEntityLookPacket }
                .subscribe {
                    val entity = entities[it.entityId] ?: return@subscribe
                    entity.onGround = it.onGround
                    entity.yaw = it.yaw
                    entity.pitch = it.pitch
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityLookAndRelativeMovePacket }
                .map { it.packet as ServerEntityLookAndRelativeMovePacket }
                .subscribe {
                    val entity = entities[it.entityId] ?: return@subscribe
                    entity.x += it.deltaX
                    entity.y += it.deltaY
                    entity.z += it.deltaZ
                    entity.onGround = it.onGround
                    entity.pitch = it.pitch
                    entity.yaw = it.yaw
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityPropertiesPacket }
                .map { it.packet as ServerEntityPropertiesPacket }
                .subscribe {
                    (entities[it.entityId] ?: return@subscribe).properties = it.properties
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityMetadataPacket }
                .map { it.packet as ServerEntityMetadataPacket }
                .subscribe {
                    (entities[it.entityId] ?: return@subscribe).metadata = it.metadata
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerDestroyEntitiesPacket }
                .map { it.packet as ServerDestroyEntitiesPacket }
                .subscribe {
                    it.entities.forEach { i -> entities.remove(i) }
                }
        }
    }

    fun loadChunk(column: Column) {
        for (y in 0 until column.chunks.size) {
            val chunk = column.chunks[y] ?: break

            val blocks = ByteArray(4096)
            val metadata = ByteArray(4096)

            for (i in blocks.indices) {
                val loc = extract(i)

                val data = chunk.blocks.get(loc[0], loc[1], loc[2]).id

                blocks[i] = (data and 0xFFF).toByte()
                metadata[i] = (data shr 12 and 0xF).toByte()
            }


            val chunkSection = ChunkSection(this, ChunkLocation(column.x, y, column.z), blocks, metadata)

            chunks[chunkSection.location] = chunkSection
        }

        logger.debug("Chunk " + column.x + " " + column.z + " loaded")
    }

    fun getBlockAt(x: Int, y: Int, z: Int): Block? {
        return getBlockAt(BlockLocation(x, y, z))
    }

    fun getBlockAt(location: BlockLocation): Block? {
        val chunkLocation = ChunkLocation(location)
        val chunk = getChunkAt(chunkLocation) ?: return null

        val chunkBlockOffset = BlockLocation(chunkLocation)
        val chunkOffsetX = location.x - chunkBlockOffset.x
        val chunkOffsetY = location.y - chunkBlockOffset.y
        val chunkOffsetZ = location.z - chunkBlockOffset.z
        val id = chunk.getBlockIdAt(chunkOffsetX, chunkOffsetY, chunkOffsetZ)
        val metadata = chunk.getBlockMetadataAt(chunkOffsetX, chunkOffsetY, chunkOffsetZ)
        return Block(this, chunk, location, id, metadata)
    }

    fun getBlockIdAt(x: Int, y: Int, z: Int): Int {
        return getBlockIdAt(BlockLocation(x, y, z))
    }

    fun getBlockIdAt(blockLocation: BlockLocation): Int {
        val location = ChunkLocation(blockLocation)
        val chunkBlockOffset = BlockLocation(location)
        val chunk = getChunkAt(location) ?: return 0

        return chunk.getBlockIdAt(
            blockLocation.x - chunkBlockOffset.x,
            blockLocation.y - chunkBlockOffset.y,
            blockLocation.z - chunkBlockOffset.z
        )
    }

    fun setBlockIdAt(id: Int, x: Int, y: Int, z: Int) {
        setBlockIdAt(id, BlockLocation(x, y, z))
    }

    fun setBlockIdAt(id: Int, blockLocation: BlockLocation) {
        val location = ChunkLocation(blockLocation)
        val chunkBlockOffset = BlockLocation(location)
        val chunk = getChunkAt(location) ?: return
        chunk.setBlockIdAt(
            id,
            blockLocation.x - chunkBlockOffset.x,
            blockLocation.y - chunkBlockOffset.y,
            blockLocation.z - chunkBlockOffset.z
        )
    }

    fun getBlockMetadataAt(x: Int, y: Int, z: Int): Int {
        return getBlockMetadataAt(BlockLocation(x, y, z))
    }

    fun getBlockMetadataAt(blockLocation: BlockLocation): Int {
        val location = ChunkLocation(blockLocation)
        val chunkBlockOffset = BlockLocation(location)
        val chunk = getChunkAt(location) ?: return 0
        return chunk.getBlockMetadataAt(
            blockLocation.x - chunkBlockOffset.x,
            blockLocation.y - chunkBlockOffset.y,
            blockLocation.z - chunkBlockOffset.z
        )
    }

    fun getChunkAt(x: Int, y: Int, z: Int): ChunkSection? {
        return getChunkAt(ChunkLocation(x, y, z))
    }

    fun getChunkAt(location: ChunkLocation): ChunkSection? {
        synchronized(chunks) {
            return chunks[location]
        }
    }

    fun dumpChunk(chunkX: Int, chunkZ: Int) {
        for (y in 0..255) {
            val image = BufferedImage(16 * 16, 16 * 16, BufferedImage.TYPE_INT_RGB)

            val graphics = image.graphics

            //            int y = 84;

            for (x in 0..15) {
                for (z in 0..15) {
                    val id = getBlockIdAt((chunkX * 16) + x, y, (chunkZ * 16) + z)

                    if (id != 0) {
                        var f = File(
                            "F:\\Projects\\IntelliJ\\proxy\\assets\\" + ProtocolVersion.V1_14_4.registryManager.blockRegistry.get(
                                id
                            )!!.name + "_top.png"
                        )

                        if (!f.exists()) {
                            f = File(
                                "F:\\Projects\\IntelliJ\\proxy\\assets\\" + ProtocolVersion.V1_14_4.registryManager.blockRegistry.get(
                                    id
                                )!!.name + ".png"
                            )
                        }

                        try {
                            val img = ImageIO.read(f)

                            graphics.drawImage(img, x * 16, z * 16, null)

                        } catch (e: IOException) {
                            println("couldn't find " + f.name)
                        }

                    }
                }
            }

            try {
                ImageIO.write(image, "PNG", File("A:\\dump\\$chunkX-$y-$chunkZ.png"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun unloadChunkColumn(x: Int, z: Int) {
        for (y in 0..15) {
            chunks.remove(ChunkLocation(x, y, z))
        }

        logger.debug("Chunk $x $z unloaded")
    }

    companion object {

        fun extract(i: Int): IntArray {
            return intArrayOf(i and 0xF, i shr 8 and 0xF, i shr 4 and 0xF)
        }
    }
}
