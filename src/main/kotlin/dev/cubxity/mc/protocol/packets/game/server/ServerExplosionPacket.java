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
import dev.cubxity.mc.protocol.utils.Vec3b;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerExplosionPacket extends Packet {
    private float x;
    private float y;
    private float z;
    private float radius;
    private List<Vec3b> records;
    private float playerMotionX;
    private float playerMotionY;
    private float playerMotionZ;

    public ServerExplosionPacket(float x, float y, float z, float radius, List<Vec3b> records, float playerMotionX, float playerMotionY, float playerMotionZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.records = records;
        this.playerMotionX = playerMotionX;
        this.playerMotionY = playerMotionY;
        this.playerMotionZ = playerMotionZ;
    }

    public ServerExplosionPacket() {
    }

    @Override
    public void read(@NotNull NetInput buf, @NotNull ProtocolVersion target) {
        x = buf.readFloat();
        y = buf.readFloat();
        z = buf.readFloat();
        radius = buf.readFloat();

        int recordCount = buf.readInt();

        records = new ArrayList<>(recordCount);

        for (int i = 0; i < recordCount; i++) {
            records.add(new Vec3b(buf.readByte(), buf.readByte(), buf.readByte()));
        }

        playerMotionX = buf.readFloat();
        playerMotionY = buf.readFloat();
        playerMotionZ = buf.readFloat();
    }

    @Override
    public void write(@NotNull NetOutput out, @NotNull ProtocolVersion target) {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
        out.writeFloat(radius);

        out.writeInt(records.size());

        for (Vec3b record : records) {
            out.writeByte(record.getX());
            out.writeByte(record.getY());
            out.writeByte(record.getZ());
        }

        out.writeFloat(playerMotionX);
        out.writeFloat(playerMotionY);
        out.writeFloat(playerMotionZ);
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

    public float getRadius() {
        return radius;
    }

    public List<Vec3b> getRecords() {
        return records;
    }

    public float getPlayerMotionX() {
        return playerMotionX;
    }

    public float getPlayerMotionY() {
        return playerMotionY;
    }

    public float getPlayerMotionZ() {
        return playerMotionZ;
    }
}
