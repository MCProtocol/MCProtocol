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
    private var decryptedArray = ByteArray(0)
    private var encryptedArray = ByteArray(0)

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val enc = session.encryption
        if (enc != null) {
            val length = msg.readableBytes()
            val bytes = getBytes(msg)
            val outLength = enc.getEncryptedSize(length)
            if (encryptedArray.size < outLength) {
                encryptedArray = ByteArray(outLength)
            }
            msg.getBytes(0, bytes)
            out.writeBytes(encryptedArray, 0, enc.encrypt(bytes, 0, length, encryptedArray, 0))
        } else out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val enc = session.encryption
        if (enc != null) {
            val length = buf.readableBytes()
            val bytes = getBytes(buf)
            val result = ctx.alloc().heapBuffer(enc.getDecryptedSize(length))
            result.writerIndex(enc.decrypt(bytes, 0, length, result.array(), result.arrayOffset()))
            out.add(result)
        } else out.add(buf.readBytes(buf.readableBytes()))
    }

    private fun getBytes(buf: ByteBuf): ByteArray {
        val length = buf.readableBytes()
        if (decryptedArray.size < length)
            decryptedArray = ByteArray(length)

        buf.readBytes(decryptedArray, 0, length)
        return decryptedArray
    }
}