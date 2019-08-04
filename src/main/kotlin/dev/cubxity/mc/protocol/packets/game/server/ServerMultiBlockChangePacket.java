/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server;

import dev.cubxity.mc.protocol.ProtocolVersion;
import dev.cubxity.mc.protocol.net.NetInput;
import dev.cubxity.mc.protocol.net.NetOutput;
import dev.cubxity.mc.protocol.packets.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerMultiBlockChangePacket extends Packet {
    private int chunkX;
    private int chunkZ;
    private List<Record> recordList;

    public ServerMultiBlockChangePacket(int chunkX, int chunkZ, List<Record> recordList) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.recordList = recordList;
    }

    public ServerMultiBlockChangePacket() {
    }

    @Override
    public void read(@NotNull NetInput buf, @NotNull ProtocolVersion target) {
        chunkX = buf.readInt();
        chunkZ = buf.readInt();

        int recordCount = buf.readVarInt();
        recordList = new ArrayList<>(recordCount);

        for (int i = 0; i < recordCount; i++) {
            int horizPos = buf.readUnsignedByte();

            recordList.add(new Record((horizPos >> 4 & 0xF) + (chunkX * 16), buf.readUnsignedByte(), (horizPos & 0xF) + (chunkZ * 16), buf.readVarInt()));
        }
    }

    @Override
    public void write(@NotNull NetOutput out, @NotNull ProtocolVersion target) {
        out.writeInt(chunkX);
        out.writeInt(chunkZ);

        out.writeVarInt(recordList.size());

        for (Record record : recordList) {
            out.writeByte(record.x << 4 | record.z);
            out.writeByte(record.y);
            out.writeVarInt(record.blockId);
        }
    }


    public static class Record {
        private int x;
        private int y;
        private int z;
        private int blockId;

        public Record(int x, int y, int z, int blockId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockId = blockId;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public int getBlockId() {
            return blockId;
        }
    }
}
