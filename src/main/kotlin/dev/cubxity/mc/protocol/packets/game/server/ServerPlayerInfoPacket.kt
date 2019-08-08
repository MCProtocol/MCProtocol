/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.magic.Gamemode
import dev.cubxity.mc.protocol.data.magic.MagicRegistry
import dev.cubxity.mc.protocol.data.magic.PlayerAction
import dev.cubxity.mc.protocol.entities.Message
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import java.util.*

class ServerPlayerInfoPacket(
    var action: PlayerAction,
    var players: Array<Player>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        action = MagicRegistry.lookupKey(target, buf.readVarInt())
        val playerCount = buf.readVarInt()

        players = arrayOf()
        for (i in 0 until playerCount) {
            val uuid = buf.readUUID()
            val player = Player(uuid)

            when (action) {
                PlayerAction.ADD_PLAYER -> {
                    with(player) {
                        name = buf.readString()
                        properties = arrayOf()

                        val propertyCount = buf.readVarInt()
                        for (j in 0 until propertyCount) {
                            val name = buf.readString()
                            val value = buf.readString()
                            val signed = buf.readBoolean()

                            properties += Property(name, value, signed, if (signed) buf.readString() else null)
                        }

                        gamemode = MagicRegistry.lookupKey(target, buf.readVarInt())
                        ping = buf.readVarInt()
                        hasDisplayName = buf.readBoolean()

                        if (hasDisplayName)
                            displayName = buf.readMessage()
                    }
                }
                PlayerAction.UPDATE_GAMEMODE -> player.gamemode = MagicRegistry.lookupKey(target, buf.readVarInt())
                PlayerAction.UPDATE_LATENCY -> player.ping = buf.readVarInt()
                PlayerAction.UPDATE_DISPLAY_NAME -> {
                    with(player) {
                        hasDisplayName = buf.readBoolean()

                        if (hasDisplayName)
                            displayName = buf.readMessage()
                    }
                }
                else -> {
                }
            }

            players += player
        }
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(MagicRegistry.lookupValue(target, action))
        out.writeVarInt(players.size)

        for (player in players) {
            out.writeUUID(player.uuid)

            when (action) {
                PlayerAction.ADD_PLAYER -> {
                    with(player) {
                        out.writeString(name)
                        out.writeVarInt(properties.size)

                        for (prop in properties) {
                            out.writeString(prop.name)
                            out.writeString(prop.value)
                            out.writeBoolean(prop.signed)

                            if (prop.signed)
                                out.writeString(prop.signature ?: return@with)
                        }

                        out.writeVarInt(MagicRegistry.lookupValue(target, gamemode))
                        out.writeVarInt(ping)
                        out.writeBoolean(hasDisplayName)

                        if (hasDisplayName)
                            out.writeMessage(displayName ?: return@with)
                    }
                }
                PlayerAction.UPDATE_GAMEMODE -> out.writeVarInt(MagicRegistry.lookupValue(target, player.gamemode))
                PlayerAction.UPDATE_LATENCY -> out.writeVarInt(player.ping)
                PlayerAction.UPDATE_DISPLAY_NAME -> {
                    with(player) {
                        out.writeBoolean(hasDisplayName)

                        if (hasDisplayName)
                            out.writeMessage(displayName ?: return@with)
                    }
                }
                else -> {
                }
            }
        }
    }

    class Player(
        var uuid: UUID,
        var name: String = "",
        var properties: Array<Property> = arrayOf(),
        var gamemode: Gamemode = Gamemode.SURVIVAL,
        var ping: Int = 0,
        var hasDisplayName: Boolean = false,
        var displayName: Message? = null
    )

    data class Property(
        val name: String,
        val value: String,
        val signed: Boolean,
        val signature: String?
    )

}