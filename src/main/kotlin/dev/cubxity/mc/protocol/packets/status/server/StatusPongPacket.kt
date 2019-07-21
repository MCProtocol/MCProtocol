package dev.cubxity.mc.protocol.packets.status.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class StatusPongPacket(var time: Long = 0) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        time = buf.readLong()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeLong(time)
    }
}