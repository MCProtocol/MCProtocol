package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.dsl.defaultProtocol
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCodec
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketEncryptor
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketSizer
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
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
    var sessionFactory: (Channel) -> ProtocolSession = { defaultProtocol(ProtocolSession.Side.SERVER, it) }
) {
    val server = TcpServer.create()
        .host(host)
        .port(port)
        .handle { i, o -> i.receive().then() }
        .doOnConnection {
            val channel = it.channel()
            val protocol = sessionFactory(channel)
            with(channel.config()) {
                setOption(ChannelOption.IP_TOS, 0x18)
                setOption(ChannelOption.TCP_NODELAY, false)
            }
            it.addHandlerLast("encryption", TcpPacketEncryptor(protocol))
            it.addHandlerLast("sizer", TcpPacketSizer())
            it.addHandlerLast("codec", TcpPacketCodec(protocol))
            it.addHandlerLast("manager", protocol)
        }

    /**
     * Builder function for session factory
     */
    fun sessionFactory(factory: (Channel) -> ProtocolSession): MCServer {
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