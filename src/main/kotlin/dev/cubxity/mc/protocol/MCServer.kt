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
    val host: String,
    val port: Int = 25565,
    val sessionFactory: (Channel) -> ProtocolSession = { defaultProtocol(ProtocolSession.Side.SERVER) }
) {
    val server = TcpServer.create()
        .host(host)
        .port(port)
        .doOnBind {
            it.childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    val protocol = sessionFactory(channel)
                    with(channel.config()) {
                        setOption(ChannelOption.IP_TOS, 0x18)
                        setOption(ChannelOption.TCP_NODELAY, false)
                    }
                    with(channel.pipeline()) {
                        addLast("encryption", TcpPacketEncryptor(protocol))
                        addLast("sizer", TcpPacketSizer())
                        addLast("codec", TcpPacketCodec(protocol))
                    }
                }
            })
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