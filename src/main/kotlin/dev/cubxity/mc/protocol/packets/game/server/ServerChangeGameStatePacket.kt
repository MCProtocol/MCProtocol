package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.GameState
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerChangeGameStatePacket(
    var state: GameState,
    var value: Float
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        state = MagicRegistry.lookupKey(target, buf.readUnsignedByte())
        value = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeByte(MagicRegistry.lookupValue(target, state))
        out.writeFloat(value)
    }
}