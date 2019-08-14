/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.bot.managers.world

import com.google.gson.GsonBuilder
import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.protocol.data.magic.*
import dev.cubxity.mc.protocol.data.obj.chunks.util.BlockUtil
import dev.cubxity.mc.protocol.entities.BlockPosition
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.client.player.ClientPlayerDiggingPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerBlockBreakAnimationPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerJoinGamePacket
import dev.cubxity.mc.protocol.packets.game.server.ServerRespawnPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerBlockChangePacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerSpawnPositionPacket
import kotlinx.coroutines.*

class WorldManager(private val bot: Bot) {

    var gamemode = Gamemode.SURVIVAL
    var dimension = Dimension.OVERWORLD
    var levelType = LevelType.DEFAULT

    private var swingJob: Job? = null
    private var finishDiggingJob: Job? = null

    private var startedDigging = System.currentTimeMillis()
    private var currentDiggingBlock: BlockPosition? = null

    init {
        with(bot.session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerSpawnPositionPacket }
                .map { it.packet as ServerSpawnPositionPacket }
                .subscribe {
                    bot.player.spawnPosition = it.position
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerJoinGamePacket }
                .map { it.packet as ServerJoinGamePacket }
                .subscribe {
                    gamemode = it.gamemode
                    dimension = it.dimension
                    levelType = it.levelType
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerChatPacket }
                .map { it.packet as ServerChatPacket }
                .subscribe {
                    val pos = bot.player.physicsManager.position.toBlockPosition()
                    breakBlock(BlockPosition(pos.x, pos.y - 1, pos.z)) {
                        println("Broken!")
                    }
                }

//            on<PacketReceivedEvent>()
//                .subscribe {
//                    println(GsonBuilder().setPrettyPrinting().create().toJson(it.packet))
//                }
        }
    }

    fun breakBlock(position: BlockPosition, cb: () -> Unit = {}) {
        stopDigging()

        bot.player.physicsManager.lookAt(SimplePosition(position.x + 0.5, position.y.toDouble(), position.z + 0.5)) {
            val state = bot.world.getBlockAt(position) ?: return@lookAt
            val data = state.lookup(bot.session.outgoingVersion) ?: return@lookAt
            val breakTime = BlockUtil.getDigTime(bot, data)

            println("BT: $breakTime")

            startedDigging = System.currentTimeMillis()
            currentDiggingBlock = position

            bot.session.send(
                ClientPlayerDiggingPacket(
                    DiggingStatus.STARTED_DIGGING,
                    position.toSimple(), BlockFace.TOP
                )
            )

            swingJob = GlobalScope.launch {
                println(isActive)
                println(System.currentTimeMillis())
                println(startedDigging + breakTime)

                while (System.currentTimeMillis() < startedDigging + breakTime && isActive) {
                    delay(350)
                    bot.player.swingArm(Hand.MAIN_HAND)
                }
            }

            finishDiggingJob = GlobalScope.launch {
                delay(breakTime.toLong() + 50)
                currentDiggingBlock = null
                bot.session.send(
                    ClientPlayerDiggingPacket(
                        DiggingStatus.FINISHED_DIGGING,
                        position.toSimple(), BlockFace.TOP
                    )
                )

                println(bot.world.getBlockAt(position)?.id)

                cb()
            }
        }
    }

    private fun stopDigging() {
        if (swingJob?.isActive == true)
            swingJob?.cancel()

        if (finishDiggingJob?.isActive == true)
            finishDiggingJob?.cancel()

        swingJob = null
        finishDiggingJob = null

        bot.session.send(
            ClientPlayerDiggingPacket(
                DiggingStatus.CANCELLED_DIGGING,
                currentDiggingBlock?.toSimple() ?: return, BlockFace.TOP
            )
        )
    }

}