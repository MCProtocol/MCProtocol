/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.utils

import dev.cubxity.mc.protocol.net.io.NetInput

fun hexdumpPacket(buf: NetInput) {
    println(hexdumpByteArray(buf.readBytes(buf.available())))
    throw IllegalStateException("Packet is being debugged...")
}

fun hexdumpByteArray(array: ByteArray): String {
    var sb = StringBuilder()

    var addr = 0

    while (addr < array.size) {
        sb.append(String.format("%08x", addr)).append("    ")

        var bytes = StringBuilder()
        var ascii = StringBuilder()

        for (i in 0 until 0x10) {
            addr++

            if (addr >= array.size) {
                bytes.append("   ")
                ascii.append(" ")
                continue
            }

            bytes.append(String.format("%02x", array[addr])).append(" ")
            ascii.append(array[addr].toChar())
        }

        sb.append(bytes).append("   | ").append(ascii).append(" |\n")
    }

    return sb.toString()
}