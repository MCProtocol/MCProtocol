package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.client
import dev.cubxity.mc.protocol.dsl.server
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginSuccessPacket

/**
 * @author Cubxity
 * @since 7/20/2019
 */
fun main() {
    client()
}

fun client() {
    client("localhost")
        .sessionFactory { con, ch ->
            buildProtocol(ProtocolSession.Side.CLIENT, con, ch) {
                applyDefaults()
                wiretap()
                login(System.getProperty("username"), System.getProperty("password"))
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
                        println("Chat: ${it.message.text}")
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