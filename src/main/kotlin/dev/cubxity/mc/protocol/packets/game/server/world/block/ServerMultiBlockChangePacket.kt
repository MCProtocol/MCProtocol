package dev.cubxity.mc.protocol.packets.game.server.world.block

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.SimplePosition
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerMultiBlockChangePacket(
    var chunkX: Int,
    var chunkZ: Int,
    var records: Array<Record>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        chunkX = buf.readInt()
        chunkZ = buf.readInt()

        var readRecords = arrayOf<Record>()

        for (i in 0 until buf.readVarInt()) {
            val horizontalPosition = buf.readUnsignedByte()

            val worldX = (horizontalPosition shr 4 and 15) + chunkX * 16
            val worldZ = (horizontalPosition and 15) + chunkZ * 16
            val worldY = buf.readUnsignedByte()

            readRecords += Record(
                horizontalPosition,
                SimplePosition(worldX.toDouble(), worldY.toDouble(), worldZ.toDouble()),
                buf.readVarInt()
            )
        }

        records = readRecords
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeInt(chunkX)
        out.writeInt(chunkZ)
        out.writeVarInt(records.size)

        for (record in records) {
            out.writeByte(record.horizontalPosition)
            out.writeByte(record.position.y.toInt())
            out.writeVarInt(record.blockId)
        }
    }

    data class Record(
        val horizontalPosition: Int,
        val position: SimplePosition,
        val blockId: Int
    )
}