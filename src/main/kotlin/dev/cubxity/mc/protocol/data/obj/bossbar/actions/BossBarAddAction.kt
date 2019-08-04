/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.bossbar.actions

import dev.cubxity.mc.protocol.data.obj.ChatComponent
import dev.cubxity.mc.protocol.data.obj.bossbar.BossBarColor
import dev.cubxity.mc.protocol.data.obj.bossbar.BossBarDivision
import dev.cubxity.mc.protocol.packets.annotations.UnsignedByte

class BossBarAddAction(
    val title: ChatComponent,
    val health: Float // From 0 to 1. Values greater than 1 do not crash a Notchian client, and start rendering part of a second health bar at around 1.5.
    ,
    val color: BossBarColor // Color ID (see below)
    ,
    val division: BossBarDivision // Type of division (see below)
    , @field:UnsignedByte
    val flags: Int // Bit mask. 0x1: should darken sky, 0x2: is dragon bar (used to play end music), 0x04: create fog (previously was also controlled by 0x02)
) : BossBarAction()
