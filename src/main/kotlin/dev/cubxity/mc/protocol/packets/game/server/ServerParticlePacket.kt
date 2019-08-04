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
import dev.cubxity.mc.protocol.data.obj.particle.AbstractParticleData
import dev.cubxity.mc.protocol.data.obj.particle.BlockParticleData
import dev.cubxity.mc.protocol.data.obj.particle.DustParticleData
import dev.cubxity.mc.protocol.data.obj.particle.ItemParticleData
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerParticlePacket : Packet {
    var particleId: Int = 0
        private set
    var isLongDistance: Boolean = false
        private set
    var x: Float = 0.toFloat()
        private set
    var y: Float = 0.toFloat()
        private set
    var z: Float = 0.toFloat()
        private set
    var offsetX: Float = 0.toFloat()
        private set
    var offsetY: Float = 0.toFloat()
    var offsetZ: Float = 0.toFloat()
    var particleData: Float = 0.toFloat()
        private set
    var particleCount: Int = 0
        private set
    var data: AbstractParticleData? = null
        private set

    constructor(
        particleId: Int,
        longDistance: Boolean,
        x: Float,
        y: Float,
        z: Float,
        offsetX: Float,
        offsetY: Float,
        offsetZ: Float,
        particleData: Float,
        particleCount: Int,
        data: AbstractParticleData
    ) {
        this.particleId = particleId
        this.isLongDistance = longDistance
        this.x = x
        this.y = y
        this.z = z
        this.offsetX = offsetX
        this.offsetY = offsetY
        this.offsetZ = offsetZ
        this.particleData = particleData
        this.particleCount = particleCount
        this.data = data
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        particleId = buf.readInt()

        isLongDistance = buf.readBoolean()

        x = buf.readFloat()
        y = buf.readFloat()
        z = buf.readFloat()

        offsetX = buf.readFloat()
        offsetX = buf.readFloat()
        offsetX = buf.readFloat()

        particleData = buf.readFloat()

        particleCount = buf.readInt()

        if (particleId == 3 || particleId == 20) {
            data = BlockParticleData(buf.readVarInt())
        } else if (particleId == 11) {
            data = DustParticleData(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat())
        } else if (particleId == 27) {
            data = ItemParticleData(buf.readSlot())
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeInt(particleId)

        out.writeBoolean(isLongDistance)

        out.writeFloat(x)
        out.writeFloat(y)
        out.writeFloat(z)

        out.writeFloat(offsetX)
        out.writeFloat(offsetY)
        out.writeFloat(offsetZ)

        out.writeFloat(particleData)

        out.writeInt(particleCount)

        if (particleId == 3 || particleId == 20) {
            if (data is BlockParticleData) {
                out.writeVarInt((data as BlockParticleData).blockState)
            } else {
                throw IllegalStateException("Data has to be of type BlockParticleData due to the particleId")
            }
        } else if (particleId == 11) {
            if (data is DustParticleData) {
                out.writeFloat((data as DustParticleData).red)
                out.writeFloat((data as DustParticleData).green)
                out.writeFloat((data as DustParticleData).blue)
                out.writeFloat((data as DustParticleData).scale)
            } else {
                throw IllegalStateException("Data has to be of type DustParticleData due to the particleId")
            }
        } else if (particleId == 27) {
            if (data is ItemParticleData) {
                out.writeSlot((data as ItemParticleData).item)
            } else {
                throw IllegalStateException("Data has to be of type ItemParticleData due to the particleId")
            }
        }
    }
}
