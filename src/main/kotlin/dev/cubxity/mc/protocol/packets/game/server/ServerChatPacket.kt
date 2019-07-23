package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.MessageType
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerChatPacket @JvmOverloads constructor(var message: Message, var type: MessageType = MessageType.CHAT) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        message = Message.fromJson(buf.readString())
        MagicRegistry.lookupKey<MessageType>(target, buf.readByte().toInt())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(message.toJson())
        out.writeByte(MagicRegistry.lookupValue(target, type))
    }
}