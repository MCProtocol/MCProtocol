/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.dsl.defaultProtocol
import dev.cubxity.mc.protocol.events.ConnectedEvent
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCodec
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCompression
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketEncryptor
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketSizer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.nio.NioSocketChannel
import reactor.netty.Connection
import reactor.netty.tcp.TcpServer

/**
 * Instance of a server
 * @param host host to bind to
 * @param port port to bind to
 * @param sessionFactory factory to provide [ProtocolSession]
 * @author Cubxity
 * @since 7/20/2019
 */
class MCServer @JvmOverloads constructor(
    val host: String = "127.0.0.1",
    val port: Int = 25565,
    var sessionFactory: (Connection, NioSocketChannel) -> ProtocolSession = { con, ch ->
        defaultProtocol(
            ProtocolSession.Side.SERVER,
            con,
            ch
        )
    }
) {
    val server = TcpServer.create()
        .host(host)
        .port(port)
        .handle { i, o -> i.receive().then() }
        .doOnConnection {
            val channel = it.channel() as NioSocketChannel
            val protocol = sessionFactory(it, channel)
            protocol.sink.next(ConnectedEvent(it))
            with(channel.config()) {
                setOption(ChannelOption.IP_TOS, 0x18)
                setOption(ChannelOption.TCP_NODELAY, false)
            }
            it.addHandlerLast("encryption", TcpPacketEncryptor(protocol))
            it.addHandlerLast("sizer", TcpPacketSizer())
            it.addHandlerLast("compression", TcpPacketCompression(protocol))
            it.addHandlerLast("codec", TcpPacketCodec(protocol))
            it.addHandlerLast("manager", protocol)
        }

    /**
     * Builder function for session factory
     */
    fun sessionFactory(factory: (Connection, NioSocketChannel) -> ProtocolSession): MCServer {
        sessionFactory = factory
        return this
    }

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