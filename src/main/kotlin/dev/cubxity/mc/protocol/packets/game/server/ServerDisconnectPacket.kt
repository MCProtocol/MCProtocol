package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/22/2019
 */
class ServerDisconnectPacket(var reason: Message) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        reason = Message.fromJson(buf.readString())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(reason.toJson())
    }
}