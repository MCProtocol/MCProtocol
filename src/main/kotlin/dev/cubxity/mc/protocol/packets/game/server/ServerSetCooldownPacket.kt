package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerSetCooldownPacket(
    var itemId: Int,
    var cooldownTicks: Int
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        itemId = buf.readVarInt()
        cooldownTicks = buf.readVarInt()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(itemId)
        out.writeVarInt(cooldownTicks)
    }
}