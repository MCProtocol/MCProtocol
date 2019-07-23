package dev.cubxity.mc.protocol.packets.game.server.block

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerMultiBlockChangePacket(
    var chunkX: Int,
    var chunkZ: Int
) : Packet() {

    var records: Array<Record> = arrayOf()

    override fun read(buf: NetInput, target: ProtocolVersion) {
        chunkX = buf.readInt()
        chunkZ = buf.readInt()

        for (i in 0..buf.readVarInt()) {
            val horizPos = buf.readByte().toInt()

            val worldX = (horizPos shr 4 and 15) + chunkX * 16
            val worldZ = (horizPos and 15) + chunkZ * 16

            val worldY = buf.readByte()

            val position = SimplePosition(worldX.toDouble(), worldY.toDouble(), worldZ.toDouble())
            records += Record(horizPos, worldY.toInt(), position, buf.readVarInt())
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeInt(chunkX)
        out.writeInt(chunkZ)
        out.writeVarInt(records.size)

        records.forEach {
            out.writeByte(it.horizPos)
            out.writeByte(it.vertPos)
            out.writeVarInt(it.blockId)
        }
    }

    data class Record(
        val horizPos: Int,
        val vertPos: Int,
        val position: SimplePosition,
        val blockId: Int
    )

}