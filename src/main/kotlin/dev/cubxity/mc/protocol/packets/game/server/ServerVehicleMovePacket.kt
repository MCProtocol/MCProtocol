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

class ServerVehicleMovePacket : Packet {
    var x: Double = 0.toDouble()
        private set
    var y: Double = 0.toDouble()
        private set
    var z: Double = 0.toDouble()
        private set

    var yaw: Float = 0.toFloat()
        private set
    var pitch: Float = 0.toFloat()
        private set

    constructor(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.yaw = yaw
        this.pitch = pitch
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()

        yaw = buf.readFloat()
        pitch = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)

        out.writeFloat(yaw)
        out.writeFloat(pitch)
    }
}
