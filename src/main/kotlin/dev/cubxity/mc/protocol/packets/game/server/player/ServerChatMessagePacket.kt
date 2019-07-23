package dev.cubxity.mc.protocol.packets.game.server.player

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.MagicRegistry
import dev.cubxity.mc.protocol.data.enums.game.EnumChatPosition
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerChatMessagePacket(
    var message: Message,
    var position: EnumChatPosition
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        message = buf.readMessage()
        position = MagicRegistry.lookupKey(target, buf.readByte())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeMessage(message)
        out.writeByte(MagicRegistry.lookupValue(target, position))
    }
}