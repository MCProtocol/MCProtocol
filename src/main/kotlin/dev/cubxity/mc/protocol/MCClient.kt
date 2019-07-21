package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.dsl.defaultProtocol
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketCodec
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketEncryptor
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketSizer
import io.netty.channel.Channel
import reactor.netty.tcp.TcpClient

/**
 * @author Cubxity
 * @since 7/20/2019
 */
class MCClient @JvmOverloads constructor(
    val host: String, val port: Int = 25565,
    var sessionFactory: (Channel) -> ProtocolSession = { defaultProtocol(ProtocolSession.Side.CLIENT, it) }
) {
    val client = TcpClient.create()
        .host(host)
        .port(port)
        .doOnConnected {
            val channel = it.channel()
            val protocol = sessionFactory(channel)
            with(channel.config()) {
                setOption(io.netty.channel.ChannelOption.IP_TOS, 0x18)
                setOption(io.netty.channel.ChannelOption.TCP_NODELAY, false)
            }
            it.addHandlerLast("encryption", TcpPacketEncryptor(protocol))
            it.addHandlerLast("sizer", TcpPacketSizer())
            it.addHandlerLast("codec", TcpPacketCodec(protocol))
            it.addHandlerLast("manager", protocol)
        }

    /**
     * Builder function for session factory
     */
    fun sessionFactory(factory: (Channel) -> ProtocolSession): MCClient {
        sessionFactory = factory
        return this
    }

    /**
     * Start connecting
     * @return [reactor.core.publisher.Mono]
     */
    fun connect() = client.connect()

    /**
     * Start connecting (sync)
     * @return [reactor.netty.Connection]
     */
    fun connectNow() = client.connectNow()
}