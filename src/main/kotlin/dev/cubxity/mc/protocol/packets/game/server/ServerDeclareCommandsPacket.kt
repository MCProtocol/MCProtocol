/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.commandData.parser.*
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerDeclareCommandsPacket : Packet() {
    var nodes: ArrayList<Node>? = null
    var rootIndex: Int = 0

    override fun read(buf: NetInput, target: ProtocolVersion) {
        val count = buf.readVarInt()

        nodes = ArrayList(count)

        for (i in 0 until count) {
            val flags = buf.readByte()

            val type = flags.toInt() and Node.TYPE_BITMASK

            val childrenCount = buf.readVarInt()

            val children = ArrayList<Int>(childrenCount)

            for (j in 0 until childrenCount) {
                children.add(buf.readVarInt())
            }

            var redirectNode: Int? = null

            if (type == Node.HAS_REDIRECT) {
                redirectNode = buf.readVarInt()
            }

            var name: String? = null


            if (type == Node.LITERAL_TYPE || type == Node.ARGUMENT_TYPE) {
                name = buf.readString()
            }

            var parser: String? = null

            if (type == Node.ARGUMENT_TYPE) {
                parser = buf.readString()


                val parserClass = when (parser) {
                    "brigadier:double" -> {
                        val flags = buf.readByte().toInt()

                        DoubleParser(
                            if ((flags and 0x01) == 1) buf.readDouble() else Double.MIN_VALUE,
                            if ((flags and 0x02) == 1) buf.readDouble() else Double.MAX_VALUE
                        )
                    }
                    "brigadier:float" -> {
                        val flags = buf.readByte().toInt()

                        FloatParser(
                            if ((flags and 0x01) == 1) buf.readFloat() else Float.MIN_VALUE,
                            if ((flags and 0x02) == 1) buf.readFloat() else Float.MAX_VALUE
                        )
                    }
                    "brigadier:integer" -> {
                        val flags = buf.readByte().toInt()

                        IntegerParser(
                            if ((flags and 0x01) == 1) buf.readInt() else Int.MIN_VALUE,
                            if ((flags and 0x02) == 1) buf.readInt() else Int.MAX_VALUE
                        )
                    }
                    "brigadier:string" -> StringParser(StringParser.Type.values()[buf.readVarInt()])
                    "minecraft:entity" -> EntityParser(buf.readByte().toInt())
                    "minecraft:score_holder" -> ScoreHolderParser(buf.readBoolean())
                    "minecraft:range" -> RangeParser(buf.readBoolean())
                    else -> SimpleParser(parser)
                }
            }

            var suggestionsType: String? = null

            if (flags.toInt() and Node.HAS_SUGGESTIONS_TYPE == 1) {
                suggestionsType = buf.readString()
            }

            nodes!!.add(Node(flags, children, redirectNode, name, parser, suggestionsType))
        }

        rootIndex = buf.readVarInt()
    }


    override fun write(out: NetOutput, target: ProtocolVersion) {
        TODO("I am too lazy and it propably won't be used /shrug")
    }

    class Node(
        val flags: Byte,
        val children: List<Int>,
        val redirectNode: Int?,
        val name: String?,
        val parser: String?,
        val suggestionsType: String?
    ) {
        companion object {
            val TYPE_BITMASK = 0x03
            val IS_EXECUTABLE = 0x04
            val HAS_REDIRECT = 0x08
            val HAS_SUGGESTIONS_TYPE = 0x08

            val ROOT_TYPE = 0x00
            val LITERAL_TYPE = 0x01
            val ARGUMENT_TYPE = 0x02
        }
    }
}
