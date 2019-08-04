/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.entity.player

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.PositionElement
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet


class ServerPlayerPositionLookPacket(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float,
    var pitch: Float,
    var relative: Array<PositionElement>,
    var teleportId: Int
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        yaw = buf.readFloat()
        pitch = buf.readFloat()

        val flags = buf.readUnsignedByte()
        for (element in PositionElement.values()) {
            val bit = 1 shl MagicRegistry.lookupValue(target, element)

            if (flags and bit == bit) {
                relative += element
            }
        }

        teleportId = buf.readVarInt()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
        out.writeFloat(yaw)
        out.writeFloat(pitch)

        var flags = 0
        for (element in relative) {
            flags = flags or (1 shl MagicRegistry.lookupValue(target, element))
        }

        out.writeByte(flags)
        out.writeVarInt(teleportId)
    }
}