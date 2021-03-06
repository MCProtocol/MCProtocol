/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.world

import com.github.steveice10.opennbt.tag.builtin.CompoundTag
import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.chunks.Chunk
import dev.cubxity.mc.protocol.data.obj.chunks.ChunkPosition
import dev.cubxity.mc.protocol.data.obj.chunks.util.ChunkUtil
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.net.io.impl.stream.StreamNetInput
import dev.cubxity.mc.protocol.packets.Packet

class ServerChunkDataPacket(
    var chunkX: Int,
    var chunkZ: Int,
    var full: Boolean,
    var bitMask: Int,
    var heightMaps: CompoundTag,
    var chunk: Chunk,
    var blockEntities: ArrayList<CompoundTag>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        chunkX = buf.readInt()
        chunkZ = buf.readInt()
        full = buf.readBoolean()
        bitMask = buf.readVarInt()
        heightMaps = buf.readNbt() as CompoundTag

        val dataSize = buf.readVarInt()
        val data = buf.readBytes(dataSize)

        blockEntities = buf.readVarArray { buf.readNbt() as CompoundTag }

        chunk = ChunkUtil.readChunkColumn(full, bitMask, StreamNetInput(data.inputStream()), target)
        chunk.position = ChunkPosition(chunkX, chunkZ)
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        TODO("Implement write")
    }
}