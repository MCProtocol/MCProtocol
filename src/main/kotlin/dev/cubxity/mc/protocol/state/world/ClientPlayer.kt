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

import dev.cubxity.mc.api.on
import dev.cubxity.mc.protocol.data.magic.PositionElement
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.client.ClientChatMessagePacket
import dev.cubxity.mc.protocol.packets.game.client.ClientTeleportConfirmPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerPlayerPositionLookPacket
import dev.cubxity.mc.protocol.state.Tracker

class ClientPlayer(private val tracker: Tracker) {

    val session = tracker.session

    var position = SimplePosition(0.0, 0.0, 0.0)
    var pitch = 0.0f
    var yaw = 0.0f
    var onGround = true

    init {
        with(tracker.session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerPlayerPositionLookPacket }
                .map { it.packet as ServerPlayerPositionLookPacket }
                .subscribe {
                    position.x = if (PositionElement.X in it.relative) position.x + it.x else it.x
                    position.y = if (PositionElement.Y in it.relative) position.y + it.y else it.y
                    position.z = if (PositionElement.Z in it.relative) position.z + it.z else it.z

                    yaw = if (PositionElement.X_ROT in it.relative) pitch + it.pitch else it.pitch
                    pitch = if (PositionElement.Y_ROT in it.relative) pitch + it.pitch else it.pitch

                    send(ClientTeleportConfirmPacket(it.teleportId))
                }
        }
    }

    fun chat(text: String) = session.send(ClientChatMessagePacket(text))
}