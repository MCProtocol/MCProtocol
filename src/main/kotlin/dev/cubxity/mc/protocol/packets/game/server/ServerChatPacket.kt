package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.MagicRegistry
import dev.cubxity.mc.protocol.data.enum.MessageType
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerChatPacket @JvmOverloads constructor(var message: Message, var type: MessageType = MessageType.CHAT) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        message = Message.fromJson(buf.readString())
        MagicRegistry.lookupKey<MessageType>(target, buf.readByte())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(message.toJson())
        MagicRegistry.lookupValue<Int>(target, type)
    }
}