/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.state

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.data.magic.MessageType
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.server.ServerChatPacket
import dev.cubxity.mc.protocol.state.world.ClientPlayer
import dev.cubxity.mc.protocol.state.world.World

class Tracker(val session: ProtocolSession) {

    var world = World(session)
    var player = ClientPlayer(this)

    init {
        registerHooks()
    }

    private fun registerHooks() {
        with (session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerChatPacket }
                .map { it.packet as ServerChatPacket }
                .filter { it.type != MessageType.NOTIFICATION }
                .subscribe { sink.next(ChatMessageReceivedEvent(it.message, it.message.toText())) }
        }
    }

}