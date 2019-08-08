/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.net.pipeline

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.net.io.impl.buf.ByteBufNetInput
import dev.cubxity.mc.protocol.net.io.impl.buf.ByteBufNetOutput
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
        val output = ByteBufNetOutput(out)
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
            val ni = ByteBufNetInput(buf)
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