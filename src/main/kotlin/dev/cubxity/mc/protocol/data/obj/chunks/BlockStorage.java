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

import dev.cubxity.mc.protocol.net.io.NetInput;
import dev.cubxity.mc.protocol.net.io.NetOutput;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BlockStorage {
    private static final BlockState AIR = new BlockState(0);

    private int blockCount;
    private int bitsPerEntry;

    private List<BlockState> states;
    private FlexibleStorage storage;

    public BlockStorage() {
        this.blockCount = 0;
        this.bitsPerEntry = 4;

        this.states = new ArrayList<BlockState>();
        this.states.add(AIR);

        this.storage = new FlexibleStorage(this.bitsPerEntry, 4096);
    }

    public BlockStorage(NetInput in) throws IOException {
        this.blockCount = in.readShort();
        this.bitsPerEntry = in.readUnsignedByte();

        this.states = new ArrayList<BlockState>();
        int stateCount = this.bitsPerEntry > 8 ? 0 : in.readVarInt();
        for (int i = 0; i < stateCount; i++) {
            this.states.add(in.readBlockState());
        }

        this.storage = new FlexibleStorage(this.bitsPerEntry, in.readLongs(in.readVarInt()));
    }

    private static int index(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private static BlockState rawToState(int raw) {
        return new BlockState(raw);
    }

    private static int stateToRaw(BlockState state) {
        return state.getId();
    }

    public void write(NetOutput out) throws IOException {
        out.writeShort((short) this.blockCount);
        out.writeByte(this.bitsPerEntry);

        if (this.bitsPerEntry <= 8) {
            out.writeVarInt(this.states.size());
            for (BlockState state : this.states) {
                out.writeBlockState(state);
            }
        }

        long[] data = this.storage.getData();
        out.writeVarInt(data.length);
        out.writeLongs(data);
    }

    public int getBitsPerEntry() {
        return this.bitsPerEntry;
    }

    public List<BlockState> getStates() {
        return Collections.unmodifiableList(this.states);
    }

    public FlexibleStorage getStorage() {
        return this.storage;
    }

    public BlockState get(int x, int y, int z) {
        int id = this.storage.get(index(x, y, z));
        return this.bitsPerEntry <= 8 ? (id >= 0 && id < this.states.size() ? this.states.get(id) : AIR) : rawToState(id);
    }

    public void set(int x, int y, int z, BlockState state) {
        int id = this.bitsPerEntry <= 8 ? this.states.indexOf(state) : stateToRaw(state);
        if (id == -1) {
            this.states.add(state);
            if (this.states.size() > 1 << this.bitsPerEntry) {
                this.bitsPerEntry++;

                List<BlockState> oldStates = this.states;
                if (this.bitsPerEntry > 8) {
                    oldStates = new ArrayList<BlockState>(this.states);
                    this.states.clear();
                    this.bitsPerEntry = 13;
                }

                FlexibleStorage oldStorage = this.storage;
                this.storage = new FlexibleStorage(this.bitsPerEntry, this.storage.getSize());
                for (int index = 0; index < this.storage.getSize(); index++) {
                    this.storage.set(index, this.bitsPerEntry <= 8 ? oldStorage.get(index) : stateToRaw(oldStates.get(index)));
                }
            }

            id = this.bitsPerEntry <= 8 ? this.states.indexOf(state) : stateToRaw(state);
        }

        int ind = index(x, y, z);
        int curr = this.storage.get(ind);
        if (state.getId() != AIR.getId() && curr == AIR.getId()) {
            this.blockCount++;
        } else if (state.getId() == AIR.getId() && curr != AIR.getId()) {
            this.blockCount--;
        }

        this.storage.set(ind, id);
    }

    public boolean isEmpty() {
        for (int index = 0; index < this.storage.getSize(); index++) {
            if (this.storage.get(index) != 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockStorage that = (BlockStorage) o;

        if (blockCount != that.blockCount) return false;
        if (bitsPerEntry != that.bitsPerEntry) return false;
        if (!Objects.equals(states, that.states)) return false;
        return Objects.equals(storage, that.storage);
    }

    @Override
    public int hashCode() {
        int result = blockCount;
        result = 31 * result + bitsPerEntry;
        result = 31 * result + (states != null ? states.hashCode() : 0);
        result = 31 * result + (storage != null ? storage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
