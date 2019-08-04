package dev.cubxity.mc.protocol.packets.game.server.entity.player

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerPlayerAbilitiesPacket(
    var invincible: Boolean,
    var canFly: Boolean,
    var flying: Boolean,
    var creative: Boolean,
    var flySpeed: Float,
    var walkSpeed: Float
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        val flags = buf.readByte().toInt()

        invincible = flags and 1 > 0
        canFly = flags and 2 > 0
        flying = flags and 4 > 0
        creative = flags and 8 > 0

        flySpeed = buf.readFloat()
        walkSpeed = buf.readFloat()
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        var flags = 0

        if (invincible) {
            flags = flags or 1
        }

        if (canFly) {
            flags = flags or 2
        }

        if (flying) {
            flags = flags or 4
        }

        if (creative) {
            flags = flags or 8
        }

        out.writeByte(flags)
        out.writeFloat(flySpeed)
        out.writeFloat(walkSpeed)
    }
}