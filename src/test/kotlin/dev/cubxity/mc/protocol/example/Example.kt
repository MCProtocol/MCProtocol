package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.ProtocolSession.Side.SERVER
import dev.cubxity.mc.protocol.dsl.buildProtocol
import dev.cubxity.mc.protocol.dsl.server


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
                wiretap()
            }
        }
        .bind()
        .doOnSuccess { println("Bound to: ${it.host()}:${it.port()}") }
        .block()!!
        .onDispose()
        .block() // Block until the server shuts down
}