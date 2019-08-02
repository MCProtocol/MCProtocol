package dev.cubxity.mc.protocol.packets.game.server.entity

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.EntityMetadata
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerEntityMetadataPacket(
    var entityId: Int,
    var metadata: Array<EntityMetadata>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        entityId = buf.readVarInt()
        metadata = buf.readEntityMetadata(target)
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(entityId)
        out.writeEntityMetadata(metadata, target)
    }
}