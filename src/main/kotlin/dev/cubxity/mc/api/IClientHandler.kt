/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.api

import dev.cubxity.mc.api.data.ClientSettings
import dev.cubxity.mc.api.data.Location
import dev.cubxity.mc.api.data.enums.Difficulty

interface IClientHandler {
    var difficulty: Difficulty
    var difficultyLocked: Boolean

    val clientSettings: ClientSettings

    /**
     * Sends out a Confirm Teleport packet
     * @param id The id of the teleport packet
     */
    fun confirmTeleport(id: Int)

    /**
     * @return Transaction Id. To verify the response
     */
    fun queryBlockNBT(blockPosition: Location): Int

    /**
     * Sends a chat message
     * @param message The message
     */
    fun sendChatMessage(message: String)

    /**
     * Sends a respawn request to the server
     */
    fun respawn() // ClientStatusPacket

    /**
     * Sends a stats request to the server
     */
    fun requestStats() // ClientStatusPacket

    /**
     * @return Transaction Id. To verify the response
     */
    fun sendTabComplete(text: String): Int

    /**
     * See https://wiki.vg/Protocol#Confirm_Transaction_.28serverbound.29
     */
    fun confirmTransaction(windowId: Int, actionNumber: Int, accepted: Boolean)

    /**
     * See https://wiki.vg/Pre-release_protocol#Click_Window_Button
     */
    fun clickWindowButton(windowId: Int, buttonId: Int)

//    fun clickWindow(windowId: Int, slot: Int, button: Int, mode: Int, );


}