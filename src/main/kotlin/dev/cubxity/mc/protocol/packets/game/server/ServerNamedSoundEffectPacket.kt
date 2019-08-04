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
import dev.cubxity.mc.protocol.data.magic.SoundCategory
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerNamedSoundEffectPacket : Packet {
    private var soundName: String? = null
    private var soundCategory: SoundCategory? = null
    private var effectPositionX: Double = 0.toDouble()
    private var effectPositionY: Double = 0.toDouble()
    private var effectPositionZ: Double = 0.toDouble()
    private var volume: Float = 0.toFloat()
    private var pitch: Float = 0.toFloat()

    constructor(
        soundName: String,
        soundCategory: SoundCategory,
        effectPositionX: Double,
        effectPositionY: Double,
        effectPositionZ: Double,
        volume: Float,
        pitch: Float
    ) {
        this.soundName = soundName
        this.soundCategory = soundCategory
        this.effectPositionX = effectPositionX
        this.effectPositionY = effectPositionY
        this.effectPositionZ = effectPositionZ
        this.volume = volume
        this.pitch = pitch
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        soundName = buf.readString()
        soundCategory = SoundCategory.values()[buf.readVarInt()]
        effectPositionX = buf.readInt() / 8.0
        effectPositionY = buf.readInt() / 8.0
        effectPositionZ = buf.readInt() / 8.0
        volume = buf.readFloat()
        pitch = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(soundName!!)
        out.writeVarInt(soundCategory!!.ordinal)
        out.writeInt((effectPositionX * 8).toInt())
        out.writeInt((effectPositionY * 8).toInt())
        out.writeInt((effectPositionZ * 8).toInt())
        out.writeFloat(volume)
        out.writeFloat(pitch)
    }
}
