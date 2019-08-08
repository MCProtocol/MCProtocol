/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

import java.util.ArrayList

class ServerMultiBlockChangePacket : Packet {
    private var chunkX: Int = 0
    private var chunkZ: Int = 0
    private var recordList: MutableList<Record>? = null

    constructor(chunkX: Int, chunkZ: Int, recordList: MutableList<Record>) {
        this.chunkX = chunkX
        this.chunkZ = chunkZ
        this.recordList = recordList
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        chunkX = buf.readInt()
        chunkZ = buf.readInt()

        val recordCount = buf.readVarInt()
        recordList = ArrayList(recordCount)

        for (i in 0 until recordCount) {
            val horizPos = buf.readUnsignedByte()

            recordList!!.add(
                Record(
                    (horizPos shr 4 and 0xF) + chunkX * 16,
                    buf.readUnsignedByte(),
                    (horizPos and 0xF) + chunkZ * 16,
                    buf.readVarInt()
                )
            )
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeInt(chunkX)
        out.writeInt(chunkZ)

        out.writeVarInt(recordList!!.size)

        for (record in recordList!!) {
            out.writeByte(record.x shl 4 or record.z)
            out.writeByte(record.y)
            out.writeVarInt(record.blockId)
        }
    }


    class Record(val x: Int, val y: Int, val z: Int, val blockId: Int)
}
