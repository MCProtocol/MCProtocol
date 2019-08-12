/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import dev.cubxity.mc.protocol.net.io.NetInput;
import dev.cubxity.mc.protocol.net.io.impl.stream.StreamNetInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ChunkUtil {

    public static Column readColumn(byte[] data, int x, int z, boolean fullChunk, boolean hasSkylight, int mask, CompoundTag[] tileEntities, CompoundTag heightMaps) throws IOException {
        NetInput in = new StreamNetInput(new ByteArrayInputStream(data));
        Throwable ex = null;
        Column column = null;
        try {
            Chunk[] chunks = new Chunk[16];
            for (int index = 0; index < chunks.length; index++) {
                if ((mask & (1 << index)) != 0) {
                    BlockStorage blocks = new BlockStorage(in);
                    chunks[index] = new Chunk(blocks);
                }
            }

            int[] biomeData = null;
            if (fullChunk) {
                biomeData = in.readInts(256);
            }

            column = new Column(x, z, chunks, biomeData, tileEntities, heightMaps);
        } catch (Throwable e) {
            ex = e;
        }

        // Unfortunately, this is needed to detect whether the chunks contain skylight or not.
        if ((in.available() > 0 || ex != null) && !hasSkylight) {
            return readColumn(data, x, z, fullChunk, true, mask, tileEntities, heightMaps);
        } else if (ex != null) {
            throw new IOException("Failed to read chunk data.", ex);
        }

        return column;
    }

}
