/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.bot.bot
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerSoundEffectPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    dev.cubxity.mc.protocol.dsl.client("localhost")
        .sessionFactory { con, ch ->
            buildProtocol(ProtocolSession.Side.CLIENT, con, ch) {
                applyDefaults()
                offline("TestUser")

                val bot = bot()

                on<PacketReceivedEvent>()
                    .filter { it.packet is ServerChatPacket }
                    .map { it.packet as ServerChatPacket }
                    .subscribe {
                        val text = it.message.toText()

                        if (text.contains("cast", true)) {
                            bot.player.inventoryManager.useItem()
                        } else if (text.contains("profit?")) {
                            bot.player.chat(bot.player.inventoryManager.inventory.slots.values.filter { s -> s.isPresent && s.itemId != 0 }.joinToString { s ->
                                val lookup =
                                    ProtocolVersion.V1_14_4.registryManager.itemRegistry.get(s.itemId)?.displayName
                                        ?: "N/A"
                                "$lookup x ${s.count}"
                            })
                        }
                    }

                on<PacketReceivedEvent>()
                    .filter { it.packet is ServerSoundEffectPacket }
                    .map { it.packet as ServerSoundEffectPacket }
                    .subscribe {
                        if (it.soundId != 62) return@subscribe

                        GlobalScope.launch {
                            bot.player.inventoryManager.useItem()
                            delay(300)
                            bot.player.inventoryManager.useItem()
                        }
                    }
            }
        }
        .connect()
        .doOnSuccess { println("Connected: $it") }
        .block()!!
        .onDispose()
        .block() // Block until the client shuts down
    Thread.sleep(100)
}