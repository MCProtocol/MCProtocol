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
import dev.cubxity.mc.protocol.data.obj.map.MapDataIcon
import dev.cubxity.mc.protocol.data.obj.map.MapDataIconType
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerMapDataPacket(
    var mapId: Int,
    var scale: Int,
    var trackingPosition: Boolean,
    var locked: Boolean,
    var icons: ArrayList<MapDataIcon>,
    var columns: Int,
    var rows: Int = 0,
    var x: Byte = 0,
    var z: Byte = 0,
    var data: ArrayList<Int>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        mapId = buf.readVarInt()
        scale = buf.readUnsignedByte()
        trackingPosition = buf.readBoolean()
        locked = buf.readBoolean()

        icons = buf.readVarArray {
            MapDataIcon(
                MapDataIconType.values()[buf.readVarInt()],
                buf.readByte(),
                buf.readByte(),
                buf.readUnsignedByte(),
                buf.readOptional { buf.readMessage() })
        }

        columns = buf.readUnsignedByte()

        if (columns > 0) {
            rows = buf.readUnsignedByte()
            x = buf.readByte()
            z = buf.readByte()
            data = buf.readVarArray { buf.readUnsignedByte() }
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        TODO("Implement this shit")
    }

}
