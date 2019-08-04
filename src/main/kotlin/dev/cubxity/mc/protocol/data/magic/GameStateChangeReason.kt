/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.magic

enum class GameStateChangeReason {
    INVALID_BED, // Would be used to switch between messages, but the only used message is 0 for invalid bed
    END_RAINING,
    BEGIN_RAINING,
    CHANGE_GAMEMODE, // 0: Survival, 1: Creative, 2: Adventure, 3: Spectator
    EXIT_END, // 0: Immediately send Client Status of respawn without showing end credits; 1: Show end credits and respawn at the end (or when esc is pressed). 1 is sent if the player has not yet received the "The end?" advancement, while if they do have it 0 is used.
    DEMO_MESSAGE, // 0: Show welcome to demo screen, 101: Tell movement controls, 102: Tell jump control, 103: Tell inventory control
    ARROW_HITTING_PLAYER, // Appears to be played when an arrow strikes another player in Multiplayer
    FADE_VALUE, // The current darkness value. 1 = Dark, 0 = Bright, Setting the value higher causes the game to change color and freeze
    FADE_TIME, // Time in ticks for the sky to fade
    PLAY_PUFFERFISH_STING_SOUND,
    PLAY_ELDER_GUARDIAN_MOB_APPEARANCE
}
