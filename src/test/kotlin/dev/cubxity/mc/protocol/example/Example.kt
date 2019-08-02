package dev.cubxity.mc.protocol.example

import com.google.gson.GsonBuilder
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.data.magic.ClientStatus
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.client
import dev.cubxity.mc.protocol.dsl.server
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.RawPacket
import dev.cubxity.mc.protocol.packets.game.client.ClientStatusPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerUpdateHealthPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginSuccessPacket

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
                wiretap()
//                login(System.getProperty("username"), System.getProperty("password"))
                offline("TestUser")

                val gson = GsonBuilder().setPrettyPrinting().create()

                on<PacketReceivedEvent>()
                    .subscribe {
                        if (it.packet is RawPacket) {
                            val id = (it.packet as RawPacket).id

                            if (id !in unknownPackets) {
                                unknownPackets += id
                            }
                        }

                        println("Packet data: ${gson.toJson(if (it.packet is RawPacket) return@subscribe else it.packet)}")
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
            }
        }
        .bind()
        .doOnSuccess { println("Bound: $it") }
        .block()!!
        .onDispose()
        .block()
}