package dev.cubxity.mc.protocol.net.pipeline

import dev.cubxity.mc.protocol.ProtocolSession
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

/**
 * @author Cubxity
 * @since 7/20/2019
 */
class TcpPacketEncryptor(private val session: ProtocolSession) : ByteToMessageCodec<ByteBuf>() {
    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val enc = session.encryption
        if (enc != null) {
            val length = msg.readableBytes()
            val bytes = ByteArray(length)
            val outArray = ByteArray(enc.getEncryptedSize(length))
            msg.getBytes(0, bytes)
            enc.encrypt(bytes, outArray)
            out.writeBytes(outArray)
        } else out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val enc = session.encryption
        if (enc != null) {
            val length = buf.readableBytes()
            val bytes = ByteArray(length)
            buf.getBytes(0, bytes)
            val result = ctx.alloc().heapBuffer(enc.getDecryptedSize(length))
            result.writerIndex(enc.decrypt(bytes, result.array()))
            out.add(result)
        } else out.add(buf.readBytes(buf.readableBytes()))
    }
}