/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.utils.Vec3b

import java.util.ArrayList

class ServerExplosionPacket : Packet {
    var x: Float = 0.toFloat()
        private set
    var y: Float = 0.toFloat()
        private set
    var z: Float = 0.toFloat()
        private set
    var radius: Float = 0.toFloat()
        private set
    private var records: MutableList<Vec3b>? = null
    var playerMotionX: Float = 0.toFloat()
        private set
    var playerMotionY: Float = 0.toFloat()
        private set
    var playerMotionZ: Float = 0.toFloat()
        private set

    constructor(
        x: Float,
        y: Float,
        z: Float,
        radius: Float,
        records: MutableList<Vec3b>,
        playerMotionX: Float,
        playerMotionY: Float,
        playerMotionZ: Float
    ) {
        this.x = x
        this.y = y
        this.z = z
        this.radius = radius
        this.records = records
        this.playerMotionX = playerMotionX
        this.playerMotionY = playerMotionY
        this.playerMotionZ = playerMotionZ
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        x = buf.readFloat()
        y = buf.readFloat()
        z = buf.readFloat()
        radius = buf.readFloat()

        val recordCount = buf.readInt()

        records = ArrayList(recordCount)

        for (i in 0 until recordCount) {
            records!!.add(Vec3b(buf.readByte(), buf.readByte(), buf.readByte()))
        }

        playerMotionX = buf.readFloat()
        playerMotionY = buf.readFloat()
        playerMotionZ = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeFloat(x)
        out.writeFloat(y)
        out.writeFloat(z)
        out.writeFloat(radius)

        out.writeInt(records!!.size)

        for (record in records!!) {
            out.writeByte(record.x.toInt())
            out.writeByte(record.y.toInt())
            out.writeByte(record.z.toInt())
        }

        out.writeFloat(playerMotionX)
        out.writeFloat(playerMotionY)
        out.writeFloat(playerMotionZ)
    }

    fun getRecords(): List<Vec3b>? {
        return records
    }
}
