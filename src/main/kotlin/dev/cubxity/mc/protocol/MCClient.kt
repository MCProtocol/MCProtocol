package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.dsl.defaultProtocol
import reactor.netty.tcp.TcpClient

/**
 * @author Cubxity
 * @since 7/20/2019
 */
class MCClient @JvmOverloads constructor(val host: String, val port: Int = 25565,
                                         val protocol: MCProtocol = defaultProtocol(MCProtocol.Side.CLIENT)) {
    val client = TcpClient.create()
        .host(host)
        .port(port)

    /**
     * Start connecting
     * @return [reactor.core.publisher.Mono]
     */
    fun bind() = client.connect()

    /**
     * Start connecting (sync)
     * @return [reactor.netty.Connection]
     */
    fun bindNow() = client.connectNow()
}