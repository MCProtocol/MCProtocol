/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks


class ChunkSection
//    private final HashMap<BlockLocation, TileEntity> tileEntities = new HashMap<BlockLocation, TileEntity>();
// TODO Implement tile entities for chests and shit, yk?


    (val world: World, val location: ChunkLocation, val blocks: ByteArray, val metadata: ByteArray) {

    fun getBlockIdAt(x: Int, y: Int, z: Int): Int {
        val index = y shl 8 or (z shl 4) or x
        return if (index < 0 || index >= blocks.size) 0 else blocks[index].toInt() and 0xFF
    }

    fun setBlockIdAt(id: Int, x: Int, y: Int, z: Int) {
        val index = y shl 8 or (z shl 4) or x
        if (index < 0 || index > blocks.size)
            return

        val location = BlockLocation(this.location.x * 16 + x, this.location.y * 16 + y, this.location.z * 16 + z)

        blocks[index] = id.toByte()
        val newBlock = if (id != 0) Block(world, this, location, id, metadata[index].toInt()) else null

        // TODO Implement block change event
    }

    fun getBlockMetadataAt(x: Int, y: Int, z: Int): Int {
        val index = y shl 8 or (z shl 4) or x
        return if (index < 0 || index > metadata.size) 0 else metadata[index].toInt() and 0xFF
    }

    fun setBlockMetadataAt(metadata: Int, x: Int, y: Int, z: Int) {
        val index = y shl 8 or (z shl 4) or x

        if (index < 0 || index > this.metadata.size)
            return

        val location = BlockLocation(this.location.x * 16 + x, this.location.y * 16 + y, this.location.z * 16 + z)
        val oldBlock = Block(world, this, location, blocks[index].toInt(), this.metadata[index].toInt())

        this.metadata[index] = metadata.toByte()

        val newBlock = Block(world, this, location, blocks[index].toInt(), metadata)

        // TODO Implement block change event
    }

    override fun toString(): String {
        return "ChunkSection{" +
                "location=" + location +
                ", blocks=byte[" + blocks.size + "]" +
                ", metadata=byte[" + metadata.size + "]" +
                '}'.toString()
    }
}
