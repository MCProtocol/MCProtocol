/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.entity.movement;

import dev.cubxity.mc.protocol.ProtocolVersion;
import dev.cubxity.mc.protocol.net.NetInput;
import dev.cubxity.mc.protocol.net.NetOutput;
import dev.cubxity.mc.protocol.packets.Packet;
import org.jetbrains.annotations.NotNull;

public class ServerEntityPacket extends Packet {
    protected boolean pos, rot;

    private int entityId;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;
    private boolean onGround;

    public ServerEntityPacket(int entityId) {
        this.entityId = entityId;

        this.pos = false;
        this.rot = false;
    }

    public ServerEntityPacket(int entityId, double x, double y, double z, boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
    }

    public ServerEntityPacket(int entityId, float yaw, float pitch, boolean onGround) {
        this.entityId = entityId;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public ServerEntityPacket(int entityId, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public ServerEntityPacket() {
    }

    @Override
    public void read(@NotNull NetInput buf, @NotNull ProtocolVersion target) {
        entityId = buf.readVarInt();

        if (pos) {
            x = buf.readShort() / 4096D;
            y = buf.readShort() / 4096D;
            z = buf.readShort() / 4096D;
        }

        if (rot) {
            yaw = buf.readAngle();
            pitch = buf.readAngle();
        }

        if (pos || rot) {
            onGround = buf.readBoolean();
        }
    }

    @Override
    public void write(@NotNull NetOutput out, @NotNull ProtocolVersion target) {
        out.writeVarInt(entityId);

        if (pos) {
            out.writeShort((short) (x * 4096));
            out.writeShort((short) (y * 4096));
            out.writeShort((short) (z * 4096));
        }

        if (rot) {
            out.writeAngle(yaw);
            out.writeAngle(pitch);
        }

        if (pos || rot) {
            out.writeBoolean(onGround);
        }
    }
}
