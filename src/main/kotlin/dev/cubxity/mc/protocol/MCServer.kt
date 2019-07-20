package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.dsl.defaultProtocol
import reactor.netty.tcp.TcpServer

/**
 * Instance of a server
 * @param host host to bind to
 * @param port port to bind to
 * @author Cubxity
 * @since 7/20/2019
 */
class MCServer @JvmOverloads constructor(
    val host: String,
    val port: Int = 25565,
    val protocol: MCProtocol = defaultProtocol(MCProtocol.Side.SERVER)
) {
    val server = TcpServer.create()
        .host(host)
        .port(port)

    /**
     * Start binding the server
     * @return [reactor.core.publisher.Flux]
     */
    fun bind() = server.bind()

    /**
     * Start binding the server (sync)
     * @return [reactor.netty.DisposableServer]
     */
    fun bindNow() = server.bindNow()
}