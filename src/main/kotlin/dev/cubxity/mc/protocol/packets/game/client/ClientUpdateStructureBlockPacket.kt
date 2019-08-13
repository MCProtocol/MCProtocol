/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.client


import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ClientUpdateStructureBlockPacket(
    var location: SimplePosition,
    var action: Int,
    var mode: Int,
    var name: String,
    var offsetX: Int,
    var offsetY: Int,
    var offsetZ: Int,
    var sizeX: Int,
    var sizeY: Int,
    var sizeZ: Int,
    var mirror: Int,
    var rotation: Int,
    var metadata: String,
    var integrity: Float,
    var seed: Long,
    var flags: Int
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        location = buf.readPosition()

        action = buf.readVarInt()
        mode = buf.readVarInt()

        name = buf.readString()

        offsetX = buf.readByte().toInt()
        offsetY = buf.readByte().toInt()
        offsetZ = buf.readByte().toInt()

        sizeX = buf.readByte().toInt()
        sizeY = buf.readByte().toInt()
        sizeZ = buf.readByte().toInt()

        mirror = buf.readVarInt()
        rotation = buf.readVarInt()

        metadata = buf.readString()
        integrity = buf.readFloat()
        seed = buf.readVarLong()
        flags = buf.readUnsignedByte()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writePosition(location)

        out.writeVarInt(action)
        out.writeVarInt(mode)

        out.writeString(name)

        out.writeByte(offsetX)
        out.writeByte(offsetY)
        out.writeByte(offsetZ)

        out.writeByte(sizeX)
        out.writeByte(sizeY)
        out.writeByte(sizeZ)

        out.writeVarInt(mirror)
        out.writeVarInt(rotation)

        out.writeString(metadata)
        out.writeFloat(integrity)
        out.writeVarLong(seed)
        out.writeByte(flags)
    }

}
