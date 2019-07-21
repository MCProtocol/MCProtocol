package dev.cubxity.mc.protocol.packets.login.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class SetCompressionPacket(var threshold: Int) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        threshold = buf.readVarInt()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(threshold)
    }
}