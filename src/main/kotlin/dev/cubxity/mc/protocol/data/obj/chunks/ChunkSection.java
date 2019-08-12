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

public class ChunkSection {
    private final World world;
    private final ChunkLocation location;
    private final byte[] blocks;
    private final byte[] metadata;
//    private final HashMap<BlockLocation, TileEntity> tileEntities = new HashMap<BlockLocation, TileEntity>();
    // TODO Implement tile entities for chests and shit, yk?


    public ChunkSection(World world, ChunkLocation location, byte[] blocks, byte[] metadata) {
        this.world = world;
        this.location = location;
        this.blocks = blocks;
        this.metadata = metadata;
    }

    public World getWorld() {
        return world;
    }

    public ChunkLocation getLocation() {
        return location;
    }

    public byte[] getBlocks() {
        return blocks;
    }

    public byte[] getMetadata() {
        return metadata;
    }

    public int getBlockIdAt(int x, int y, int z) {
        int index = y << 8 | z << 4 | x;
        if (index < 0 || index > blocks.length)
            return 0;
        return blocks[index] & 0xFF;
    }

    public void setBlockIdAt(int id, int x, int y, int z) {
        int index = y << 8 | z << 4 | x;
        if (index < 0 || index > blocks.length)
            return;

        BlockLocation location = new BlockLocation((this.location.getX() * 16) + x, (this.location.getY() * 16) + y, (this.location.getZ() * 16) + z);

        blocks[index] = (byte) id;
        Block newBlock = id != 0 ? new Block(world, this, location, id, metadata[index]) : null;

        // TODO Implement block change event
    }

    public int getBlockMetadataAt(int x, int y, int z) {
        int index = y << 8 | z << 4 | x;
        if (index < 0 || index > metadata.length)
            return 0;
        return metadata[index] & 0xFF;
    }

    public void setBlockMetadataAt(int metadata, int x, int y, int z) {
        int index = y << 8 | z << 4 | x;

        if (index < 0 || index > this.metadata.length)
            return;

        BlockLocation location = new BlockLocation((this.location.getX() * 16) + x, (this.location.getY() * 16) + y, (this.location.getZ() * 16) + z);
        Block oldBlock = new Block(world, this, location, blocks[index], this.metadata[index]);

        this.metadata[index] = (byte) metadata;

        Block newBlock = new Block(world, this, location, blocks[index], metadata);

        // TODO Implement block change event
    }

    @Override
    public String toString() {
        return "ChunkSection{" +
                "location=" + location +
                ", blocks=byte[" + blocks.length + "]" +
                ", metadata=byte[" + metadata.length + "]" +
                '}';
    }
}
