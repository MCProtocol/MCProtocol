package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.dsl.defaultProtocol
import dev.cubxity.mc.protocol.net.pipeline.TcpPacketEncryptor
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import reactor.netty.tcp.TcpClient

/**
 * @author Cubxity
 * @since 7/20/2019
 */
class MCClient @JvmOverloads constructor(
    val host: String, val port: Int = 25565,
    val protocol: MCProtocol = defaultProtocol(MCProtocol.Side.CLIENT)
) {
    val client = TcpClient.create()
        .host(host)
        .port(port)
        .doOnConnect {
            it.handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    with(channel.config()) {
                        setOption(ChannelOption.IP_TOS, 0x18)
                        setOption(ChannelOption.TCP_NODELAY, false)
                    }
                    with(channel.pipeline()) {
                        addLast("encryption", TcpPacketEncryptor(protocol))
                    }
                }
            })
        }

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