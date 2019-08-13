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
import dev.cubxity.mc.protocol.data.obj.NibbleArray3d
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.io.IOException

class ServerUpdateLightPacket(
    var x: Int,
    var z: Int,
    var skyLight: ArrayList<NibbleArray3d?>,
    var blockLight: ArrayList<NibbleArray3d?>
) : Packet() {
    companion object {
        val EMPTY = NibbleArray3d(4096)
    }

    override fun read(buf: NetInput, target: ProtocolVersion) {
        this.x = buf.readVarInt()
        this.z = buf.readVarInt()

        val skyLightMask = buf.readVarInt()
        val blockLightMask = buf.readVarInt()
        val emptySkyLightMask = buf.readVarInt()
        val emptyBlockLightMask = buf.readVarInt()

        this.skyLight = ArrayList(18)
        for (i in 0..17) {
            when {
                skyLightMask and (1 shl i) != 0 -> {
                    if (buf.readVarInt() != 2048) {
                        throw IOException("Expected sky light byte array to be of length 2048")
                    }
                    this.skyLight.add(NibbleArray3d(buf, 2048)) // 2048 bytes read = 4096 entries
                }
                emptySkyLightMask and (1 shl i) != 0 -> this.skyLight.add(NibbleArray3d(4096))
                else -> this.skyLight.add(null)
            }
        }

        this.blockLight = ArrayList(18)

        for (i in 0..17) {
            when {
                blockLightMask and (1 shl i) != 0 -> {
                    if (buf.readVarInt() != 2048) {
                        throw IOException("Expected block light byte array to be of length 2048")
                    }
                    this.blockLight.add(NibbleArray3d(buf, 2048)) // 2048 bytes read = 4096 entries
                }
                emptyBlockLightMask and (1 shl i) != 0 -> this.blockLight.add(NibbleArray3d(4096))
                else -> this.blockLight.add(null)
            }
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(this.x)
        out.writeVarInt(this.z)

        var skyLightMask = 0
        var blockLightMask = 0
        var emptySkyLightMask = 0
        var emptyBlockLightMask = 0

        for (i in 0..17) {
            val skyLight = this.skyLight[i]
            if (skyLight != null) {
                if (EMPTY == skyLight) {
                    emptySkyLightMask = emptySkyLightMask or (1 shl i)
                } else {
                    skyLightMask = skyLightMask or (1 shl i)
                }
            }
            val blockLight = this.blockLight[i]
            if (blockLight != null) {
                if (EMPTY == blockLight) {
                    emptyBlockLightMask = emptyBlockLightMask or (1 shl i)
                } else {
                    blockLightMask = blockLightMask or (1 shl i)
                }
            }
        }

        out.writeVarInt(skyLightMask)
        out.writeVarInt(blockLightMask)
        out.writeVarInt(emptySkyLightMask)
        out.writeVarInt(emptyBlockLightMask)

        for (i in 0..17) {
            if (skyLightMask and (1 shl i) != 0) {
                out.writeVarInt(2048)
                this.skyLight[i]!!.write(out)
            }
        }

        for (i in 0..17) {
            if (blockLightMask and (1 shl i) != 0) {
                out.writeVarInt(2048)
                this.blockLight[i]!!.write(out)
            }
        }
    }

}
