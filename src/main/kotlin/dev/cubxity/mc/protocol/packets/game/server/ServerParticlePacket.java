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
import dev.cubxity.mc.protocol.data.obj.particle.AbstractParticleData;
import dev.cubxity.mc.protocol.data.obj.particle.BlockParticleData;
import dev.cubxity.mc.protocol.data.obj.particle.DustParticleData;
import dev.cubxity.mc.protocol.data.obj.particle.ItemParticleData;
import dev.cubxity.mc.protocol.net.NetInput;
import dev.cubxity.mc.protocol.net.NetOutput;
import dev.cubxity.mc.protocol.packets.Packet;
import org.jetbrains.annotations.NotNull;

public class ServerParticlePacket extends Packet {
    private int particleId;
    private boolean longDistance;
    private float x;
    private float y;
    private float z;
    private float offsetX;
    private float offsetY;
    private float offsetZ;
    private float particleData;
    private int particleCount;
    private AbstractParticleData data;

    public ServerParticlePacket(int particleId, boolean longDistance, float x, float y, float z, float offsetX, float offsetY, float offsetZ, float particleData, int particleCount, AbstractParticleData data) {
        this.particleId = particleId;
        this.longDistance = longDistance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.particleData = particleData;
        this.particleCount = particleCount;
        this.data = data;
    }

    public ServerParticlePacket() {
    }

    @Override
    public void read(@NotNull NetInput buf, @NotNull ProtocolVersion target) {
        particleId = buf.readInt();

        longDistance = buf.readBoolean();

        x = buf.readFloat();
        y = buf.readFloat();
        z = buf.readFloat();

        offsetX = buf.readFloat();
        offsetX = buf.readFloat();
        offsetX = buf.readFloat();

        particleData = buf.readFloat();

        particleCount = buf.readInt();

        if (particleId == 3 || particleId == 20) {
            data = new BlockParticleData(buf.readVarInt());
        } else if (particleId == 11) {
            data = new DustParticleData(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        } else if (particleId == 27) {
            data = new ItemParticleData(buf.readSlot());
        }
    }

    @Override
    public void write(@NotNull NetOutput out, @NotNull ProtocolVersion target) {
        out.writeInt(particleId);

        out.writeBoolean(longDistance);

        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);

        out.writeFloat(offsetX);
        out.writeFloat(offsetY);
        out.writeFloat(offsetZ);

        out.writeFloat(particleData);

        out.writeInt(particleCount);

        if (particleId == 3 || particleId == 20) {
            if (data instanceof BlockParticleData) {
                out.writeVarInt(((BlockParticleData) data).getBlockState());
            } else {
                throw new IllegalStateException("Data has to be of type BlockParticleData due to the particleId");
            }
        } else if (particleId == 11) {
            if (data instanceof DustParticleData) {
                out.writeFloat(((DustParticleData) data).getRed());
                out.writeFloat(((DustParticleData) data).getGreen());
                out.writeFloat(((DustParticleData) data).getBlue());
                out.writeFloat(((DustParticleData) data).getScale());
            } else {
                throw new IllegalStateException("Data has to be of type DustParticleData due to the particleId");
            }
        } else if (particleId == 27) {
            if (data instanceof ItemParticleData) {
                out.writeSlot(((ItemParticleData) data).getItem());
            } else {
                throw new IllegalStateException("Data has to be of type ItemParticleData due to the particleId");
            }
        }
    }

    public int getParticleId() {
        return particleId;
    }

    public boolean isLongDistance() {
        return longDistance;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public float getParticleData() {
        return particleData;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public AbstractParticleData getData() {
        return data;
    }
}
