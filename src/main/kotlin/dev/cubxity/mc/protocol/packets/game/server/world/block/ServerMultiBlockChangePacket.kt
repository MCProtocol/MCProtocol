/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.world.block

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerMultiBlockChangePacket(
    var chunkX: Int,
    var chunkZ: Int,
    var records: Array<Record>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        chunkX = buf.readInt()
        chunkZ = buf.readInt()

        var readRecords = arrayOf<Record>()

        for (i in 0 until buf.readVarInt()) {
            val horizontalPosition = buf.readUnsignedByte()

            val worldX = (horizontalPosition shr 4 and 15) + chunkX * 16
            val worldZ = (horizontalPosition and 15) + chunkZ * 16
            val worldY = buf.readUnsignedByte()

            readRecords += Record(
                horizontalPosition,
                SimplePosition(worldX.toDouble(), worldY.toDouble(), worldZ.toDouble()),
                buf.readVarInt()
            )
        }

        records = readRecords
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeInt(chunkX)
        out.writeInt(chunkZ)
        out.writeVarInt(records.size)

        for (record in records) {
            out.writeByte(record.horizontalPosition)
            out.writeByte(record.position.y.toInt())
            out.writeVarInt(record.blockId)
        }
    }

    data class Record(
        val horizontalPosition: Int,
        val position: SimplePosition,
        val blockId: Int
    )
}