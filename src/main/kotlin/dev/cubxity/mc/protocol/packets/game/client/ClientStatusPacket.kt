package dev.cubxity.mc.protocol.packets.game.client

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.ClientStatus
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ClientStatusPacket(
    var action: ClientStatus
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        action = MagicRegistry.lookupKey(target, buf.readVarInt())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(MagicRegistry.lookupValue(target, action))
    }
}