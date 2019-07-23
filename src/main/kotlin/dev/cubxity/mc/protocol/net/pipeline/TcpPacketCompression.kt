package dev.cubxity.mc.protocol.net.pipeline

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.slf4j.LoggerFactory
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class TcpPacketCompression(private val session: ProtocolSession) : ByteToMessageCodec<ByteBuf>() {
    companion object {
        private const val MAX_COMPRESSED_SIZE = 2097152
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val deflater = Deflater()
    private val inflater = Inflater()
    private val buf = ByteArray(8192)

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        if (!session.enableCompression) {
            out.writeBytes(msg)
            return
        }
        val readable = msg.readableBytes()
        val output = NetOutput(out)
        if (readable < session.compressionThreshold) {
            output.writeVarInt(0)
            out.writeBytes(msg)
        } else {
            val bytes = ByteArray(readable)
            msg.readBytes(bytes)
            output.writeVarInt(bytes.size)
            deflater.setInput(bytes, 0, readable)
            deflater.finish()
            while (!this.deflater.finished()) {
                val length = deflater.deflate(buf)
                output.writeBytes(buf, length)
            }
            deflater.reset()
        }
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        if (!session.enableCompression) {
            out.add(buf.readBytes(buf.readableBytes()))
            return
        }
        if (buf.readableBytes() != 0) {
            val ni = NetInput(buf)
            val size = ni.readVarInt()
            if (size == 0) {
                out.add(buf.readBytes(buf.readableBytes()))
            } else {
                val threshold = this.session.compressionThreshold

                if (size < threshold) {
                    logger.error("Badly compressed packet: size of $size is below threshold of $threshold.")
                    return
                }

                if (size > MAX_COMPRESSED_SIZE) {
                    logger.error("Badly compressed packet: size of $size is larger than protocol maximum of $MAX_COMPRESSED_SIZE.")
                    return
                }

                val bytes = ByteArray(buf.readableBytes())
                ni.readBytes(bytes)
                inflater.setInput(bytes)
                val inflated = ByteArray(size)
                inflater.inflate(inflated)
                out.add(Unpooled.wrappedBuffer(inflated))
                inflater.reset()
            }
        }
    }
}