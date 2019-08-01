package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerTabCompletePacket(
    var id: Int,
    var start: Int,
    var length: Int,
    var matches: Array<Match>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        id = buf.readVarInt()
        start = buf.readVarInt()
        length = buf.readVarInt()

        var readMatches = arrayOf<Match>()

        for (i in 0..buf.readVarInt()) {
            val match = buf.readString()
            val hasTooltip = buf.readBoolean()

            readMatches += Match(match, hasTooltip, if (hasTooltip) Message.fromJson(buf.readString()) else null)
        }

        matches = readMatches
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(id)
        out.writeVarInt(start)
        out.writeVarInt(length)

        for (match in matches) {
            out.writeString(match.match)
            out.writeBoolean(match.hasTooltip)

            if (match.hasTooltip)
                out.writeString(match.tooltip?.toJson() ?: "")
        }
    }

    data class Match(
        val match: String,
        val hasTooltip: Boolean,
        val tooltip: Message?
    )
}