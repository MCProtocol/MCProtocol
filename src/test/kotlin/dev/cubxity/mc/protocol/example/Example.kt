package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.ProtocolSession.Side.SERVER
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.server
import dev.cubxity.mc.protocol.packets.PassthroughPacket
import dev.cubxity.mc.protocol.packets.handshake.client.HandshakePacket


/**
 * @author Cubxity
 * @since 7/20/2019
 */
fun main() {
    server()
        .sessionFactory { ch ->
            println("New session: ${ch.remoteAddress()}")
            buildProtocol(SERVER, ch) {
                applyDefaults()
                onPacket<HandshakePacket>()
                    .subscribe { println("Handshake: ${it.intent}") }
                onPacket<PassthroughPacket>()
                    .subscribe { println("Packet being passthrough: ${it.id}") }
            }
        }
        .bind()
        .doOnSuccess {
            println("Bound to: ${it.host()}:${it.port()}")
        }
        .block()!!
        .onDispose()
        .block()
}