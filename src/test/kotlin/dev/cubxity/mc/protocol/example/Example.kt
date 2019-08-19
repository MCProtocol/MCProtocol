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
import dev.cubxity.mc.api.on
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.data.magic.ClientStatus
import dev.cubxity.mc.protocol.data.magic.GameState
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.client
import dev.cubxity.mc.protocol.dsl.msg
import dev.cubxity.mc.protocol.dsl.server
import dev.cubxity.mc.protocol.entities.ServerListData
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.client.ClientStatusPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChangeGameStatePacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerDeclareRecipesPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerJoinGamePacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerUpdateHealthPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.ServerSpawnPlayerPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginSuccessPacket
import dev.cubxity.mc.protocol.packets.status.client.StatusQueryPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusResponsePacket

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

    var sneaking = false
    var entityId = -1

    client("localhost")
        .sessionFactory { con, ch ->
            buildProtocol(ProtocolSession.Side.CLIENT, con, ch) {
                applyDefaults()
                wiretap { it is ServerDeclareRecipesPacket }
//                login(System.getProperty("username"), System.getProperty("password"))
                offline("TestUser")

                val tracker = createTracker()

                val gson = GsonBuilder().setPrettyPrinting().create()

                on<PacketReceivedEvent>()
                    .subscribe {
                        /*if (it.packet is RawPacket) {
                            val id = (it.packet as RawPacket).id

                            if (id !in unknownPackets) {
                                unknownPackets += id
                            }
                        }*/
//                        logger.debug("[$side - RECEIVED]: ${if (it.packet is RawPacket) "RawPacket: id: 0x${(it.packet as RawPacket).id.toString(16)}" else "${it.packet}"}")
                        //println("Packet data: ${gson.toJson(if (it.packet is RawPacket || it.packet is ServerChunkDataPacket) return@subscribe else it.packet)}")
                    }

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

//                        ch.writeAndFlush(
//                            ClientEntityActionPacket(
//                                entityId,
//                                if (sneaking) EntityAction.START_SNEAKING else EntityAction.STOP_SNEAKING,
//                                0
//                            )
//                        )
//                        sneaking = !sneaking


//                        ch.writeAndFlush(ClientUseEntityPacket(3856, InteractionType.INTERACT, hand = EnumHand.MAIN_HAND))
//                        println()
//                        tracker.world.dumpChunk(-2, 1)
//                        println(tracker.session.incomingVersion.registryManager.blockRegistry.get(tracker.world.getBlockAt(BlockPosition(-32, 11, 25))!!.blockId))
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
                on<PacketReceivedEvent>()
                    .filter { it.packet is ServerJoinGamePacket }
                    .map { it.packet as ServerJoinGamePacket }
                    .subscribe {
                        entityId = it.entityId
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
                on<PacketReceivedEvent>()
                    .filter { it.packet is StatusQueryPacket }
                    .next()
                    .subscribe {
                        send(
                            StatusResponsePacket(
                                ServerListData(
                                    ServerListData.Version("MCProtocol", outgoingVersion.id),
                                    msg("MCProtocol Server"),
                                    ServerListData.Players(1, 0)
                                )
                            )
                        )
                    }
            }
        }
        .bind()
        .doOnSuccess { println("Bound: $it") }
        .block()!!
        .onDispose()
        .block()
}