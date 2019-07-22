package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.ProtocolSession.Side.CLIENT
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.client
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginSuccessPacket


/**
 * @author Cubxity
 * @since 7/20/2019
 */
fun main() {
    client("mc.hypixel.net")
        .sessionFactory { ch ->
            buildProtocol(CLIENT, ch) {
                applyDefaults()
                wiretap()
                login(System.getProperty("username"), System.getProperty("password"))
                on<PacketReceivedEvent<LoginSuccessPacket>>()
                    .next()
                    .subscribe {
                        println("Login success!")
                    }
                on<PacketReceivedEvent<ServerChatPacket>>()
                    .subscribe { (packet) ->
                        println("Chat: ${packet.message.text}")
                    }
            }
        }
        .connect()
        .doOnSuccess { println("Connected: $it") }
        .block()!!
        .onDispose()
        .block() // Block until the client shuts down
}