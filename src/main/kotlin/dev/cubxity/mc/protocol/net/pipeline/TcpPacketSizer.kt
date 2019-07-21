package dev.cubxity.mc.protocol.net.pipeline

import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.slf4j.LoggerFactory

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class TcpPacketSizer : ByteToMessageCodec<ByteBuf>() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val length = msg.readableBytes()
        out.ensureWritable(getLengthSize(length) + length)
        NetOutput(out).writeVarInt(length)
        out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        buf.markReaderIndex()
        val lengthBytes = ByteArray(5)
        for (i in lengthBytes.indices) {
            if (!buf.isReadable) {
                buf.resetReaderIndex()
                return
            }
            lengthBytes[i] = buf.readByte()
            if (lengthBytes[i] >= 0 || i == 4) {
                val length = NetInput(Unpooled.wrappedBuffer(lengthBytes)).readVarInt()
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex()
                    return
                }
                out.add(buf.readBytes(length))
                return
            }
        }

        logger.error("Length is too long")
    }

    fun getLengthSize(length: Int) = when {
        length and -128 == 0 -> 1
        length and -16384 == 0 -> 2
        length and -2097152 == 0 -> 3
        length and -268435456 == 0 -> 4
        else -> 5
    }
}