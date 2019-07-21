package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.ProtocolSession.Side.CLIENT
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.server
import dev.cubxity.mc.protocol.packets.PassthroughPacket


/**
 * @author Cubxity
 * @since 7/20/2019
 */
fun main() {
    server()
        .sessionFactory { ch ->
            println("New session: ${ch.remoteAddress()}")
            buildProtocol(CLIENT, ch) {
                applyDefaults()
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