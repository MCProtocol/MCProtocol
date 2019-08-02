package dev.cubxity.mc.protocol.packets.game.server.world

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerSpawnPositionPacket(
    var position: SimplePosition
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        position = buf.readPosition()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writePosition(position)
    }
}