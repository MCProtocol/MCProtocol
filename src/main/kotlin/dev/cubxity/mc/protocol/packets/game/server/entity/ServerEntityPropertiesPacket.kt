/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.entity

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.ModifierOperation
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerEntityPropertiesPacket(
    var entityId: Int,
    var properties: Array<Property>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        properties = arrayOf()

        val propertyCount = buf.readInt()
        for (i in 0 until propertyCount) {
            val key = buf.readString()
            val value = buf.readDouble()
            var modifiers = arrayOf<Modifier>()

            val modifierCount = buf.readVarInt()
            for (j in 0 until modifierCount) {
                val uuid = buf.readUUID()
                val amount = buf.readDouble()
                val operation = buf.readByte().toInt()

                modifiers += Modifier(
                    uuid,
                    amount,
                    MagicRegistry.lookupKey(target, operation)
                )
            }

            properties += Property(key, value, modifiers)
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeInt(properties.size)

        for (property in properties) {
            out.writeString(property.key)
            out.writeDouble(property.value)
            out.writeVarInt(property.modifiers.size)

            for (modifier in property.modifiers) {
                out.writeUUID(modifier.uuid)
                out.writeDouble(modifier.amount)
                out.writeByte(MagicRegistry.lookupValue(target, modifier.operation))
            }
        }
    }

    class Property(
        var key: String,
        var value: Double,
        var modifiers: Array<Modifier>
    )

    class Modifier(
        var uuid: UUID,
        var amount: Double,
        var operation: ModifierOperation
    )

}