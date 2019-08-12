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

import dev.cubxity.mc.bot.entity.WorldEntity
import dev.cubxity.mc.bot.entity.impl.WorldPlayerEntity
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.data.magic.Difficulity
import dev.cubxity.mc.protocol.data.magic.MobType
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor

class World(session: ProtocolSession) {

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
                    difficultyLocked = it.locked
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
                    entity.x += it.deltaX / (128.0 * 32.0)
                    entity.y += it.deltaY / (128.0 * 32.0)
                    entity.z += it.deltaZ / (128.0 * 32.0)
                    entity.onGround = it.onGround
                    entity.pitch = it.pitch
                    entity.yaw = it.yaw
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityRelativeMovePacket }
                .map { it.packet as ServerEntityRelativeMovePacket }
                .subscribe {
                    val entity = entities[it.entityId] ?: return@subscribe
                    entity.x += it.deltaX / (128.0 * 32.0)
                    entity.y += it.deltaY / (128.0 * 32.0)
                    entity.z += it.deltaZ / (128.0 * 32.0)
                    entity.onGround = it.onGround
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerEntityVelocityPacket }
                .map { it.packet as ServerEntityVelocityPacket }
                .subscribe {
                    val entity = entities[it.entityId] ?: return@subscribe

                    entity.velX = it.velocityX.toDouble()
                    entity.velY = it.velocityY.toDouble()
                    entity.velZ = it.velocityZ.toDouble()

                    entity.x += entity.velX
                    entity.y += entity.velY
                    entity.z += entity.velZ
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
        val chunkX = floor(position.x / 16.0).toInt()
        val chunkZ = floor(position.z / 16.0).toInt()
        return chunks[ChunkPosition(chunkX, chunkZ)]?.getState(position.x, position.y, position.z)
    }

}