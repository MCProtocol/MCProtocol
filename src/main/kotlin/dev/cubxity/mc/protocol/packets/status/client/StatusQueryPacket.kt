package dev.cubxity.mc.protocol.packets.status.client

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class StatusQueryPacket : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
    }
}