package dev.cubxity.mc.protocol.packets.game.server.world

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerTimeUpdatePacket(
    var worldAge: Long,
    var timeOfDay: Long
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        worldAge = buf.readLong()
        timeOfDay = buf.readLong()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeLong(worldAge)
        out.writeLong(timeOfDay)
    }
}