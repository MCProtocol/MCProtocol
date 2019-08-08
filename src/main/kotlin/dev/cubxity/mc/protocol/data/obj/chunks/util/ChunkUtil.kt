/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks.util

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.chunks.*
import dev.cubxity.mc.protocol.net.io.impl.stream.StreamNetInput


object ChunkUtil {

    fun readChunkColumn(full: Boolean, mask: Int, data: StreamNetInput, target: ProtocolVersion): Chunk {
        val chunk = Chunk()

        var sectionY = 0
        while (sectionY < chunkHeight / sectionHeight) {
            if (mask and (1 shl sectionY) != 0) {  // Is the given bit set in the mask?
                val blockCount = data.readShort()
                val bitsPerBlock = data.readUnsignedByte()
                val palette = choosePalette(bitsPerBlock.toByte(), target)
                palette.read(data)

                val individualValueMask = (1 shl bitsPerBlock) - 1

                val dataArrayLength = data.readVarInt()
                val dataArray = data.readInts(dataArrayLength)

                val section = ChunkSection()

                for (y in 0 until sectionHeight) {
                    for (z in 0 until sectionWidth) {
                        for (x in 0 until sectionWidth) {
                            val blockNumber = (y * sectionHeight + z) * sectionWidth + x
                            val startLong = blockNumber * bitsPerBlock / 64
                            val startOffset = blockNumber * bitsPerBlock % 64
                            val endLong = ((blockNumber + 1) * bitsPerBlock - 1) / 64

                            var blockData = if (startLong == endLong) {
                                dataArray[startLong] shr startOffset
                            } else {
                                val endOffset = 64 - startOffset
                                dataArray[startLong] shr startOffset or (dataArray[endLong] shl endOffset)
                            }

                            blockData = blockData and individualValueMask

                            // data should always be valid for the palette
                            // If you're reading a power of 2 minus one (15, 31, 63, 127, etc...) that's out of bounds,
                            // you're probably reading light data instead

                            val state = palette.getStateForId(blockData)
                            section.setState(x, y, z, state)
                        }
                    }
                }

//                for (y in 0 until SECTION_HEIGHT) {
//                    for (z in 0 until SECTION_WIDTH) {
//                        var x = 0
//                        while (x < SECTION_WIDTH) {
//                            // Note: x += 2 above; we read 2 values along x each time
//                            val value = ReadByte(data)
//
//                            section.SetBlockLight(x, y, z, value and 0xF)
//                            section.SetBlockLight(x + 1, y, z, value shr 4 and 0xF)
//                            x += 2
//                        }
//                    }
//                }
//
//                if (currentDimension.HasSkylight()) { // IE, current dimension is overworld / 0
//                    for (y in 0 until SECTION_HEIGHT) {
//                        for (z in 0 until SECTION_WIDTH) {
//                            var x = 0
//                            while (x < SECTION_WIDTH) {
//                                // Note: x += 2 above; we read 2 values along x each time
//                                val value = ReadByte(data)
//
//                                section.SetSkyLight(x, y, z, value and 0xF)
//                                section.SetSkyLight(x + 1, y, z, value shr 4 and 0xF)
//                                x += 2
//                            }
//                        }
//                    }
//                }

                // May replace an existing section or a null one
                chunk.sections[sectionY] = section
            }

            sectionY++
        }

        for (z in 0 until sectionWidth) {
            for (x in 0 until sectionWidth) {
                val biome = data.readInt()
//                chunk.SetBiome(x, z, data.readInt())
            }
        }

        return chunk
    }

}