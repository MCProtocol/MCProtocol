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
import dev.cubxity.mc.protocol.data.obj.Trade
import dev.cubxity.mc.protocol.data.obj.VillagerLevel
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerTradeListPacket(
    var windowId: Int,
    var trades: ArrayList<Trade>,
    var villagerLevel: VillagerLevel,
    var experience: Int,
    var isRegularVillager: Boolean,
    var canRestock: Boolean
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        windowId = buf.readVarInt()

        trades = buf.readVarArray({ buf.readUnsignedByte() }) {
            Trade(
                buf.readSlot(),
                buf.readSlot(),
                buf.readOptional { buf.readSlot() },
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readFloat(),
                buf.readInt()
            )
        }

        villagerLevel = VillagerLevel.values()[buf.readVarInt()]
        experience = buf.readVarInt()
        isRegularVillager = buf.readBoolean()
        canRestock = buf.readBoolean()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        TODO("Implement write")
    }

}
