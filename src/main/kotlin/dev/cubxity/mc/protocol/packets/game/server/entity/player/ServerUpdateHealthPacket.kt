package dev.cubxity.mc.protocol.packets.game.server.entity.player

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerUpdateHealthPacket(
    var health: Float,
    var food: Int,
    var foodSaturation: Float
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        health = buf.readFloat()
        food = buf.readVarInt()
        foodSaturation = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeFloat(health)
        out.writeVarInt(food)
        out.writeFloat(foodSaturation)
    }
}