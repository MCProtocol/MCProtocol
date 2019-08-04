/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.net.pipeline

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.slf4j.LoggerFactory

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class TcpPacketCodec(val session: ProtocolSession) : ByteToMessageCodec<Packet>() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun encode(ctx: ChannelHandlerContext, packet: Packet, buf: ByteBuf) {
        val out = NetOutput(buf)
        val id = session.getOutgoingId(packet)
        if (id == null) {
            logger.error("THIS SHOULD NEVER HAPPEN. ID not found for packet: ${packet.javaClass.simpleName}")
            return
        }
        out.writeVarInt(id)
        packet.write(out, session.outgoingVersion)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val initial = buf.readerIndex()
        val ni = NetInput(buf)
        try {
            val id = ni.readVarInt()
            if (id == -1) {
                ni.readerIndex(initial)
                return
            }

            val packet = session.createIncomingPacketById(id)

            try {
                packet.read(ni, session.incomingVersion)
            } catch (e: Exception) {
                logger.error("An error occurred whilst reading packet ${packet.javaClass.simpleName}", e)
                return
            }

            if (buf.readableBytes() > 0)
                logger.warn("Packet ${packet.javaClass.simpleName} was was not fully read")

            out.add(packet)
        } catch (e: Exception) {
            logger.error("Error occurred whilst decoding", e)
        }
    }
}