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