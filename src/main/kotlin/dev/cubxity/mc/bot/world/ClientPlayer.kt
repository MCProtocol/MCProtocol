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

import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.bot.managers.physics.PhysicsManager
import dev.cubxity.mc.bot.managers.player.PlayerManager
import dev.cubxity.mc.bot.managers.world.WorldManager
import dev.cubxity.mc.protocol.data.magic.Hand
import dev.cubxity.mc.protocol.data.magic.MobType
import dev.cubxity.mc.protocol.data.magic.PositionElement
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.client.ClientChatMessagePacket
import dev.cubxity.mc.protocol.packets.game.client.ClientTeleportConfirmPacket
import dev.cubxity.mc.protocol.packets.game.client.player.ClientAnimationPacket
import dev.cubxity.mc.protocol.packets.game.client.player.ClientPlayerPositionLookPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerPlayerPositionLookPacket
import dev.cubxity.mc.protocol.utils.ConversionUtil
import dev.cubxity.mc.protocol.utils.Vec3d
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.sqrt

class ClientPlayer(private val bot: Bot) {

    val session = bot.session

    val physicsManager = PhysicsManager(bot)
    val worldManager = WorldManager(bot)
    val playerManager = PlayerManager(bot)

    var spawnPosition = SimplePosition(0.0, 0.0, 0.0)

    init {
        with(bot.session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerPlayerPositionLookPacket }
                .map { it.packet as ServerPlayerPositionLookPacket }
                .subscribe {
                    physicsManager.position.x = if (PositionElement.X in it.relative) physicsManager.position.x + it.x else it.x
                    physicsManager.position.y = if (PositionElement.Y in it.relative) physicsManager.position.y + it.y else it.y
                    physicsManager.position.z = if (PositionElement.Z in it.relative) physicsManager.position.z + it.z else it.z

                    val packetYaw = ConversionUtil.fromNotchianYaw(it.yaw)
                    val packetPitch = ConversionUtil.fromNotchianPitch(it.pitch)

                    physicsManager.yaw = if (PositionElement.X_ROT in it.relative) physicsManager.yaw + packetYaw else packetYaw
                    physicsManager.pitch = if (PositionElement.Y_ROT in it.relative) physicsManager.pitch + packetPitch else packetPitch

                    send(ClientTeleportConfirmPacket(it.teleportId))
                }
        }
    }

    fun chat(text: String) = session.send(ClientChatMessagePacket(text))
    fun swingArm(hand: Hand) = session.send(ClientAnimationPacket(hand))
}