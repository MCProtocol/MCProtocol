package dev.cubxity.mc.protocol.net.pipeline

import dev.cubxity.mc.protocol.ProtocolSession
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

/**
 * @author Cubxity
 * @since 7/20/2019
 */
class TcpPacketEncryptor(val session: ProtocolSession) : ByteToMessageCodec<ByteBuf>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val enc = session.encryption
        if (enc != null) {

        } else out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {

    }
}