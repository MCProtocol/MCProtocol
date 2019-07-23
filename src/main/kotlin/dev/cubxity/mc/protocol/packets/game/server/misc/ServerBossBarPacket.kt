package dev.cubxity.mc.protocol.packets.game.server.misc

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.enums.bossbar.EnumBossBarAction
import dev.cubxity.mc.protocol.data.enums.bossbar.EnumBossBarColor
import dev.cubxity.mc.protocol.data.enums.bossbar.EnumBossBarDivisions
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerBossBarPacket(
    var uuid: UUID,
    var action: EnumBossBarAction,
    var title: Message = Message(),
    var health: Float = 1.0f,
    var color: EnumBossBarColor = EnumBossBarColor.PINK,
    var divisions: EnumBossBarDivisions = EnumBossBarDivisions.NO_DIVISION,
    var darkenSky: Boolean = false,
    var dragonBar: Boolean = false,
    var createFog: Boolean = false
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        uuid = buf.readUUID()
        action = MagicRegistry.lookupKey(target, buf.readVarInt())

        when (action) {
            EnumBossBarAction.ADD -> {
                title = buf.readMessage()
                health = buf.readFloat()
                color = MagicRegistry.lookupKey(target, buf.readVarInt())
                divisions = MagicRegistry.lookupKey(target, buf.readVarInt())

                val flags = buf.readByte().toInt()
                darkenSky = flags and 0x1 == 0x1
                dragonBar = flags and 0x2 == 0x2
                createFog = flags and 0x4 == 0x4
            }
            EnumBossBarAction.UPDATE_HEALTH -> {
                health = buf.readFloat()
            }
            EnumBossBarAction.UPDATE_TITLE -> {
                title = buf.readMessage()
            }
            EnumBossBarAction.UPDATE_STYLE -> {
                color = MagicRegistry.lookupKey(target, buf.readVarInt())
                divisions = MagicRegistry.lookupKey(target, buf.readVarInt())
            }
            EnumBossBarAction.UPDATE_FLAGS -> {
                val flags = buf.readByte().toInt()
                darkenSky = flags and 0x1 == 0x1
                dragonBar = flags and 0x2 == 0x2
                createFog = flags and 0x4 == 0x4
            }
            EnumBossBarAction.REMOVE -> {
            }
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeUUID(uuid)
        out.writeVarInt(MagicRegistry.lookupValue(target, action))

        when (action) {
            EnumBossBarAction.ADD -> {
                out.writeMessage(title)
                out.writeFloat(health)
                out.writeVarInt(MagicRegistry.lookupValue(target, color))
                out.writeVarInt(MagicRegistry.lookupValue(target, divisions))

                var flags = 0

                if (darkenSky) {
                    flags = flags or 0x1
                }
                if (dragonBar) {
                    flags = flags or 0x2
                }
                if (createFog) {
                    flags = flags or 0x4
                }

                out.writeByte(flags)
            }
            EnumBossBarAction.UPDATE_HEALTH -> {
                out.writeFloat(health)
            }
            EnumBossBarAction.UPDATE_TITLE -> {
                out.writeMessage(title)
            }
            EnumBossBarAction.UPDATE_STYLE -> {
                out.writeVarInt(MagicRegistry.lookupValue(target, color))
                out.writeVarInt(MagicRegistry.lookupValue(target, divisions))
            }
            EnumBossBarAction.UPDATE_FLAGS -> {
                var flags = 0

                if (darkenSky) {
                    flags = flags or 0x1
                }
                if (dragonBar) {
                    flags = flags or 0x2
                }
                if (createFog) {
                    flags = flags or 0x4
                }

                out.writeByte(flags)
            }
            EnumBossBarAction.REMOVE -> {
            }
        }
    }
}