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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Arrays;
import java.util.Objects;

public class Column {
    private int x;
    private int z;
    private Chunk[] chunks;
    private int[] biomeData;
    private CompoundTag[] tileEntities;
    private CompoundTag heightMaps;

    private boolean skylight;

    public Column(int x, int z, Chunk[] chunks, CompoundTag[] tileEntities, CompoundTag heightMaps) {
        this(x, z, chunks, null, tileEntities, heightMaps);
    }

    public Column(int x, int z, Chunk[] chunks, int[] biomeData, CompoundTag[] tileEntities, CompoundTag heightMaps) {
        if (chunks.length != 16) {
            throw new IllegalArgumentException("Chunk array length must be 16.");
        }

        if (biomeData != null && biomeData.length != 256) {
            throw new IllegalArgumentException("Biome data array length must be 256.");
        }

        this.x = x;
        this.z = z;
        this.chunks = chunks;
        this.biomeData = biomeData;
        this.tileEntities = tileEntities != null ? tileEntities : new CompoundTag[0];
        this.heightMaps = heightMaps;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public Chunk[] getChunks() {
        return this.chunks;
    }

    public boolean hasBiomeData() {
        return this.biomeData != null;
    }

    public int[] getBiomeData() {
        return this.biomeData;
    }

    public CompoundTag[] getTileEntities() {
        return this.tileEntities;
    }

    public boolean hasSkylight() {
        return this.skylight;
    }

    public CompoundTag getHeightMaps() {
        return this.heightMaps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Column)) return false;

        Column that = (Column) o;
        return this.x == that.x &&
                this.z == that.z &&
                Arrays.equals(this.chunks, that.chunks) &&
                Arrays.equals(this.biomeData, that.biomeData) &&
                Arrays.equals(this.tileEntities, that.tileEntities) &&
                Objects.equals(this.heightMaps, that.heightMaps);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}