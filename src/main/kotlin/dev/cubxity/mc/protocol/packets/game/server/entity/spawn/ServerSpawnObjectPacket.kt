/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server.entity.spawn

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.MobType
import dev.cubxity.mc.protocol.data.magic.ObjectType
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerSpawnObjectPacket(
    var entityId: Int,
    var objectUuid: UUID,
    var type: MobType,
    var x: Double,
    var y: Double,
    var z: Double,
    var pitch: Float,
    var yaw: Float,
    var data: Int,
    var velocityX: Short,
    var velocityY: Short,
    var velocityZ: Short
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        objectUuid = buf.readUUID()
        type = MagicRegistry.lookupKey(target, buf.readVarInt()) ?: return
        x = buf.readDouble()
        y = buf.readDouble()
        z = buf.readDouble()
        pitch = buf.readAngle()
        yaw = buf.readAngle()
        data = buf.readInt()
        velocityX = buf.readVelocity()
        velocityY = buf.readVelocity()
        velocityZ = buf.readVelocity()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeUUID(objectUuid)
        out.writeVarInt(MagicRegistry.lookupValue(target, type))
        out.writeDouble(x)
        out.writeDouble(y)
        out.writeDouble(z)
        out.writeAngle(pitch)
        out.writeAngle(yaw)
        out.writeInt(data)
        out.writeShort((velocityX * 8000.0).toShort())
        out.writeShort((velocityY * 8000.0).toShort())
        out.writeShort((velocityZ * 8000.0).toShort())
    }
}