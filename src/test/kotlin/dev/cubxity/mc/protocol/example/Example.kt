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

import com.google.gson.GsonBuilder
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.data.magic.ClientStatus
import dev.cubxity.mc.protocol.data.magic.GameState
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.client
import dev.cubxity.mc.protocol.dsl.server
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.RawPacket
import dev.cubxity.mc.protocol.packets.game.client.ClientStatusPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChangeGameStatePacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerUpdateHealthPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnPlayerPacket
import dev.cubxity.mc.protocol.packets.game.server.world.ServerChunkDataPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginSuccessPacket
import dev.cubxity.mc.protocol.state.ChatMessageReceivedEvent
import java.io.File

/**
 * @author Cubxity
 * @since 7/20/2019
 */
fun main() {
    client()
}

fun client() {
    var unknownPackets = arrayOf<Int>()

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Unknown packets are: ${unknownPackets.joinToString()}")
    })

    client("localhost")
        .sessionFactory { con, ch ->
            buildProtocol(ProtocolSession.Side.CLIENT, con, ch) {
                applyDefaults()
//                wiretap()
//                login(System.getProperty("username"), System.getProperty("password"))
                offline("TestUser")

                val tracker = tracker()

                val gson = GsonBuilder().setPrettyPrinting().create()

//                on<PacketReceivedEvent>()
//                    .subscribe {
//                        if (it.packet is RawPacket) {
//                            val id = (it.packet as RawPacket).id
//
//                            if (id !in unknownPackets) {
//                                unknownPackets += id
//                            }
//                        }
//
//                        logger.debug("[$side - RECEIVED]: ${it.packet.javaClass.simpleName} ${if (it.packet is RawPacket) "id: ${(it.packet as RawPacket).id}" else ""}")
//                        println("Packet data: ${gson.toJson(if (it.packet is RawPacket || it.packet is ServerChunkDataPacket) return@subscribe else it.packet)}")
//                    }

                on<PacketReceivedEvent>()
                    .filter { it.packet is LoginSuccessPacket }
                    .next()
                    .subscribe {
                        println("Login success!")
                    }

                on<PacketReceivedEvent>()
                    .filter { it.packet is ServerChatPacket }
                    .map { it.packet as ServerChatPacket }
                    .subscribe {
                        println("Chat: ${it.message.toText()}")
                    }

                on<PacketReceivedEvent>()
                    .filter { it.packet is ServerUpdateHealthPacket }
                    .map { it.packet as ServerUpdateHealthPacket }
                    .subscribe {
                        println("Player health: ${it.health}")

                        if (it.health <= 0) {
                            println("Player is dead, respawning.")
                            ch.writeAndFlush(ClientStatusPacket(ClientStatus.PERFORM_RESPAWN))
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

fun server() {
    server()
        .sessionFactory { con, ch ->
            buildProtocol(ProtocolSession.Side.SERVER, con, ch) {
                applyDefaults()
                wiretap()

                on<PacketReceivedEvent>()
                    .filter { it.packet is ServerSpawnPlayerPacket }
                    .map { it.packet as ServerSpawnPlayerPacket }
                    .subscribe {
                        ch.writeAndFlush(ServerChangeGameStatePacket(GameState.DEMO_MESSAGE, 0.0f))
                    }
            }
        }
        .bind()
        .doOnSuccess { println("Bound: $it") }
        .block()!!
        .onDispose()
        .block()
}