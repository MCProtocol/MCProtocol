/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.world

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.SoundCategory
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerSoundEffectPacket(
    var sound: String,
    var soundCategory: SoundCategory,
    var x: Double,
    var y: Double,
    var z: Double,
    var volume: Float,
    var pitch: Float
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
//        sound = target.registryManager.soundRegistry.getName(buf.readVarInt()) ?: return
        soundCategory = MagicRegistry.lookupKey(target, buf.readVarInt())
        x = buf.readInt() / 8.0
        y = buf.readInt() / 8.0
        z = buf.readInt() / 8.0
        volume = buf.readFloat()
        pitch = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
//        out.writeVarInt(target.registryManager.soundRegistry.getId(sound) ?: return)
        out.writeVarInt(MagicRegistry.lookupValue(target, soundCategory))
        out.writeInt((x * 8).toInt())
        out.writeInt((y * 8).toInt())
        out.writeInt((z * 8).toInt())
        out.writeFloat(volume)
        out.writeFloat(pitch)
    }
}