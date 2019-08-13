/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.bot.world

import dev.cubxity.mc.bot.BlockAddedEvent
import dev.cubxity.mc.bot.BlockRemovedEvent
import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.bot.entity.WorldEntity
import dev.cubxity.mc.bot.entity.impl.WorldPlayerEntity
import dev.cubxity.mc.protocol.data.magic.Difficulity
import dev.cubxity.mc.protocol.data.obj.chunks.BlockState
import dev.cubxity.mc.protocol.data.obj.chunks.Chunk
import dev.cubxity.mc.protocol.data.obj.chunks.ChunkPosition
import dev.cubxity.mc.protocol.data.obj.chunks.util.BlockUtil
import dev.cubxity.mc.protocol.entities.BlockPosition
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerDifficultyPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerUnloadChunkPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.*
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnMobPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnPlayerPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerChunkDataPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerTimeUpdatePacket
import dev.cubxity.mc.protocol.packets.game.server.world.block.ServerBlockChangePacket
import dev.cubxity.mc.protocol.packets.game.server.world.block.ServerMultiBlockChangePacket
import dev.cubxity.mc.protocol.utils.Vec3d
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.floor

class World(private val bot: Bot) {

    private val chunks = hashMapOf<ChunkPosition, Chunk>()

    val entities = ConcurrentHashMap<Int, WorldEntity>()

    var timeOfDay: Long = 0
    var worldAge: Long = 0

    var difficulty = Difficulity.NORMAL
    var difficultyLocked = false

    init {
        with(bot.session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerChunkDataPacket }
                .map { it.packet as ServerChunkDataPacket }
                .subscribe {
                    val pos = ChunkPosition(it.chunkX, it.chunkZ)
                    chunks[pos] = it.chunk

                    it.chunk.sections.forEach { (y, c) ->
                        c.states.forEach { (i, s) ->
                            sink.next(BlockAddedEvent(BlockPosition(it.chunkX * 16 + i.x, y * 16 + i.y, it.chunkZ * 16 + i.z), s))
                        }
                    }
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerUnloadChunkPacket }
                .map { it.packet as ServerUnloadChunkPacket }
                .subscribe {
                    chunks[ChunkPosition(it.chunkX, it.chunkZ)]?.sections?.forEach { (y, c) ->
                        c.states.forEach { (i, s) ->
                            sink.next(BlockRemovedEvent(BlockPosition(it.chunkX * 16 + i.x, y * 16 + i.y, it.chunkZ * 16 + i.z), s))
                        }
                    }

                    chunks.remove(ChunkPosition(it.chunkX, it.chunkZ))
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerBlockChangePacket }
                .map { it.packet as ServerBlockChangePacket }
                .subscribe {
                    val state = BlockUtil.getStateFromGlobalPaletteID(it.blockId, bot.session.outgoingVersion)
                    val pos = it.location.toBlockPosition()

                    if (it.blockId == 0) {
                        sink.next(BlockRemovedEvent(pos, state))
                    } else {
                        sink.next(BlockAddedEvent(pos, state))
                    }

                    setBlockAt(pos, state)
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerMultiBlockChangePacket }
                .map { it.packet as ServerMultiBlockChangePacket }
                .subscribe {
                    it.records.forEach { v ->
                        val state = BlockUtil.getStateFromGlobalPaletteID(v.blockId, bot.session.outgoingVersion)
                        val pos = v.position.toBlockPosition()

                        if (v.blockId == 0) {
                            sink.next(BlockRemovedEvent(pos, state))
                        } else {
                            sink.next(BlockAddedEvent(pos, state))
                        }

                        setBlockAt(pos, state)
                    }
                }

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
                    difficultyLocked = it.locked
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerSpawnPlayerPacket }
                .map { it.packet as ServerSpawnPlayerPacket }
                .subscribe {
                    entities[it.entityId] =
                        WorldPlayerEntity(
                            it.entityId,
                            SimplePosition(it.x, it.y, it.z),
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
                    entities[it.entityId] =
                        WorldEntity(
                            it.type, it.entityId, SimplePosition(it.x, it.y, it.z), Vec3d(0.0, 0.0, 0.0),
                            false, 0f, it.pitch, it.yaw
                        )
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
                    entity.pos.x += it.deltaX / (128.0 * 32.0)
                    entity.pos.y += it.deltaY / (128.0 * 32.0)
                    entity.pos.z += it.deltaZ / (128.0 * 32.0)
                    entity.onGround = it.onGround
                    entity.pitch = it.pitch
                    entity.yaw = it.yaw
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityRelativeMovePacket }
                .map { it.packet as ServerEntityRelativeMovePacket }
                .subscribe {
                    val entity = entities[it.entityId] ?: return@subscribe
                    entity.pos.x += it.deltaX / (128.0 * 32.0)
                    entity.pos.y += it.deltaY / (128.0 * 32.0)
                    entity.pos.z += it.deltaZ / (128.0 * 32.0)
                    entity.onGround = it.onGround
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityVelocityPacket }
                .map { it.packet as ServerEntityVelocityPacket }
                .subscribe {
                    val entity = entities[it.entityId] ?: return@subscribe

                    entity.vel.x = it.velocityX.toDouble()
                    entity.vel.y = it.velocityY.toDouble()
                    entity.vel.z = it.velocityZ.toDouble()

                    entity.pos.x += entity.vel.x
                    entity.pos.y += entity.vel.y
                    entity.pos.z += entity.vel.z
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

    fun isChunkLoaded(position: BlockPosition): Boolean {
        val chunkX = floor(position.x / 16.0).toInt()
        val chunkZ = floor(position.z / 16.0).toInt()
        return chunks[ChunkPosition(chunkX, chunkZ)] != null
    }

    fun getBlockAt(position: BlockPosition): BlockState? {
        val chunkX = floor(position.x / 16.0).toInt()
        val chunkZ = floor(position.z / 16.0).toInt()
        return chunks[ChunkPosition(chunkX, chunkZ)]?.getState(position.x, position.y, position.z)
    }

    fun setBlockAt(position: BlockPosition, blockState: BlockState) {
        val chunkX = floor(position.x / 16.0).toInt()
        val chunkZ = floor(position.z / 16.0).toInt()
        chunks[ChunkPosition(chunkX, chunkZ)]?.setState(position.x, position.y, position.z, blockState)
    }

    fun findClosestEntity(radius: Int = 1, filter: (WorldEntity) -> Boolean = { true }): WorldEntity? {
        val player = bot.player.physicsManager.position.toVec3()

        var closestDistance = Double.MAX_VALUE
        var closestEntity: WorldEntity? = null

        entities.values.forEach {
            if (!filter(it)) return@forEach

            val pos = it.pos.toVec3()
            val distance = pos.distanceTo(player)

            if (distance <= closestDistance) {
                closestDistance = distance
                closestEntity = it
            }
        }

        return if (closestDistance <= radius) closestEntity else null
    }

}