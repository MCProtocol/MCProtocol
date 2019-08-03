package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.annotations.UnsignedByte

class ServerAnimationPacket : Packet {
    private var entityId: Int = 0
    @UnsignedByte
    private var animationId: Int = 0

    constructor(entityId: Int, animationId: Short) {
        this.entityId = entityId
        this.animationId = animationId.toInt()
    }

    constructor()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        animationId = buf.readUnsignedByte()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeByte(animationId and 0xFF)
    }
}
