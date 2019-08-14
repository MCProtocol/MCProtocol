/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.state.world

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.data.magic.Difficulity
import dev.cubxity.mc.protocol.data.obj.chunks.BlockState
import dev.cubxity.mc.protocol.data.obj.chunks.Chunk
import dev.cubxity.mc.protocol.data.obj.chunks.ChunkPosition
import dev.cubxity.mc.protocol.entities.BlockPosition
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerDifficultyPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerUnloadChunkPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.*
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnMobPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnPlayerPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerChunkDataPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerTimeUpdatePacket
import dev.cubxity.mc.protocol.state.entity.WorldEntity
import dev.cubxity.mc.protocol.state.entity.impl.WorldPlayerEntity
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class World(val session: ProtocolSession) {
    private val chunks = hashMapOf<ChunkPosition, Chunk>()

    val entities = hashMapOf<Int, WorldEntity>()

    var timeOfDay: Long = 0
    var worldAge: Long = 0

    var difficulty = Difficulity.NORMAL
    var difficultyLocked = false

    init {
        with(session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerChunkDataPacket }
                .map { it.packet as ServerChunkDataPacket }
                .subscribe {
                    val pos = ChunkPosition(it.chunkX, it.chunkZ)
                    chunks[pos] = it.chunk

                    logger.debug("Chunk column ${it.chunkX} ${it.chunkZ} loaded")
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerUnloadChunkPacket }
                .map { it.packet as ServerUnloadChunkPacket }
                .subscribe { chunks.remove(ChunkPosition(it.chunkX, it.chunkZ)) }

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
                    entities[it.entityId] =
                        WorldPlayerEntity(it.entityId, it.x, it.y, it.z, false, 0f, it.pitch, it.yaw, it.playerUuid)
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerSpawnMobPacket }
                .map { it.packet as ServerSpawnMobPacket }
                .subscribe {
                    entities[it.entityId] =
                        WorldEntity(it.type, it.entityId, it.x, it.y, it.z, false, 0f, it.pitch, it.yaw)
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

    fun getBlockAt(position: BlockPosition): BlockState? {
        val chunkX = position.x shr 4
        val chunkZ = position.z shr 4
        return chunks[ChunkPosition(chunkX, chunkZ)]?.getState(position.x, position.y, position.z)
    }

    fun dumpChunk(chunkX: Int, chunkZ: Int) {
        for (y in 0..255) {
            println(y)
            val image = BufferedImage(16 * 16, 16 * 16, BufferedImage.TYPE_INT_RGB)

            val graphics = image.graphics

            //            int y = 84;

            for (x in 0..15) {
                for (z in 0..15) {
                    val c = getBlockAt(BlockPosition((chunkX shl 4) + x, y, (chunkZ shl 4) + z))

                    val id = c?.blockId ?: 0

                    if (id != 0) {
                        val shit = session.incomingVersion.registryManager.blockRegistry.get(
                            id
                        )
                        var f = File(
                            "F:\\Projects\\IntelliJ\\proxy\\assets\\" + shit!!.name + "_top.png"
                        )

                        if (!f.exists()) {
                            f = File(
                                "F:\\Projects\\IntelliJ\\proxy\\assets\\" + session.incomingVersion.registryManager.blockRegistry.get(
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
                    //                    System.out.printf("%03d ", id);
                }


                //                System.out.println();
            }

            try {
                ImageIO.write(image, "PNG", File("A:/aids/$chunkX-$y-$chunkZ.png"))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            //            System.out.println();
        }
        //        System.out.println();
    }

}