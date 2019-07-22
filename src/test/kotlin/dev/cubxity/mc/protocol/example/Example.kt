package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.ProtocolSession.Side.CLIENT
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.client
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
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
                on<PacketReceivedEvent<LoginSuccessPacket>>()
                    .subscribe {
                        println("Login success!")
                    }
            }
        }
        .connect()
        .doOnSuccess { println("Connected: $it") }
        .block()!!
        .onDispose()
        .block() // Block until the client shuts down
}