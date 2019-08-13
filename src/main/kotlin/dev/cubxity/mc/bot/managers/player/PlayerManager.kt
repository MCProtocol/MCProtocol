/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.bot.managers.player

import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.protocol.data.magic.PlayerAction
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerPlayerInfoPacket
import java.util.*

class PlayerManager(private val bot: Bot) {

    val playerInfo = hashMapOf<UUID, ServerPlayerInfoPacket.Player>()

    init {
        with(bot.session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerPlayerInfoPacket }
                .map { it.packet as ServerPlayerInfoPacket }
                .subscribe {
                    when (it.action) {
                        PlayerAction.ADD_PLAYER -> playerInfo.putAll(it.players.map { p -> p.uuid to p })
                        PlayerAction.UPDATE_GAMEMODE -> it.players.forEach { p -> playerInfo[p.uuid]?.gamemode = p.gamemode }
                        PlayerAction.UPDATE_LATENCY -> it.players.forEach { p -> playerInfo[p.uuid]?.ping = p.ping }
                        PlayerAction.UPDATE_DISPLAY_NAME -> it.players.forEach { p ->
                            val player = playerInfo[p.uuid] ?: return@forEach
                            player.hasDisplayName = p.hasDisplayName
                            player.displayName = p.displayName
                        }
                        PlayerAction.REMOVE_PLAYER -> it.players.forEach { p -> playerInfo.remove(p.uuid) }
                    }
                }
        }
    }

}