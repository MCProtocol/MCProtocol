/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.bot.managers.inventory

import com.google.gson.annotations.Expose
import dev.cubxity.mc.bot.Bot
import dev.cubxity.mc.protocol.events.PacketReceivedEvent
import dev.cubxity.mc.protocol.packets.game.client.ClientHeldItemChangePacket
import dev.cubxity.mc.protocol.packets.game.server.ServerSetSlotPacket
import dev.cubxity.mc.protocol.packets.game.server.ServerWindowItemsPacket

class InventoryManager(private val bot: Bot) {

    var inventory = PlayerInventory()

    init {
        with(bot.session) {
            on<PacketReceivedEvent>()
                .filter { it.packet is ServerWindowItemsPacket }
                .map { it.packet as ServerWindowItemsPacket }
                .subscribe {
                    if (it.windowId != 0) return@subscribe

                    it.slotData.forEachIndexed { i, v -> inventory.set(i, v) }
                }

            on<PacketReceivedEvent>()
                .filter { it.packet is ServerSetSlotPacket }
                .map { it.packet as ServerSetSlotPacket }
                .subscribe {
                    if (it.windowId != 0) return@subscribe

                    inventory.set(it.slot, it.data)
                }
        }
    }

    fun swapItem(hotbar: Int) {
        bot.session.send(ClientHeldItemChangePacket(hotbar))
    }

}