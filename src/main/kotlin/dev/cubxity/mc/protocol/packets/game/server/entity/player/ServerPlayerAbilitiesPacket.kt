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
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerPlayerAbilitiesPacket(
    var invincible: Boolean,
    var canFly: Boolean,
    var flying: Boolean,
    var creative: Boolean,
    var flySpeed: Float,
    var walkSpeed: Float
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        val flags = buf.readByte().toInt()

        invincible = flags and 1 > 0
        canFly = flags and 2 > 0
        flying = flags and 4 > 0
        creative = flags and 8 > 0

        flySpeed = buf.readFloat()
        walkSpeed = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        var flags = 0

        if (invincible) {
            flags = flags or 1
        }

        if (canFly) {
            flags = flags or 2
        }

        if (flying) {
            flags = flags or 4
        }

        if (creative) {
            flags = flags or 8
        }

        out.writeByte(flags)
        out.writeFloat(flySpeed)
        out.writeFloat(walkSpeed)
    }
}