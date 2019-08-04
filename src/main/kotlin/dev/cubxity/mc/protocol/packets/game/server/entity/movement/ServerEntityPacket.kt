/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.entity.movement

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

open class ServerEntityPacket : Packet {
    protected var pos: Boolean = false
    protected var rot: Boolean = false

    private var entityId: Int = 0

    private var x: Double = 0.toDouble()
    private var y: Double = 0.toDouble()
    private var z: Double = 0.toDouble()

    private var yaw: Float = 0.toFloat()
    private var pitch: Float = 0.toFloat()
    private var onGround: Boolean = false

    constructor(entityId: Int) {
        this.entityId = entityId

        this.pos = false
        this.rot = false
    }

    constructor(entityId: Int, x: Double, y: Double, z: Double, onGround: Boolean) {
        this.entityId = entityId
        this.x = x
        this.y = y
        this.z = z
        this.onGround = onGround
    }

    constructor(entityId: Int, yaw: Float, pitch: Float, onGround: Boolean) {
        this.entityId = entityId
        this.yaw = yaw
        this.pitch = pitch
        this.onGround = onGround
    }

    constructor(entityId: Int, x: Double, y: Double, z: Double, yaw: Float, pitch: Float, onGround: Boolean) {
        this.entityId = entityId
        this.x = x
        this.y = y
        this.z = z
        this.yaw = yaw
        this.pitch = pitch
        this.onGround = onGround
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()

        if (pos) {
            x = buf.readShort() / 4096.0
            y = buf.readShort() / 4096.0
            z = buf.readShort() / 4096.0
        }

        if (rot) {
            yaw = buf.readAngle()
            pitch = buf.readAngle()
        }

        if (pos || rot) {
            onGround = buf.readBoolean()
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)

        if (pos) {
            out.writeShort((x * 4096).toShort())
            out.writeShort((y * 4096).toShort())
            out.writeShort((z * 4096).toShort())
        }

        if (rot) {
            out.writeAngle(yaw)
            out.writeAngle(pitch)
        }

        if (pos || rot) {
            out.writeBoolean(onGround)
        }
    }
}
