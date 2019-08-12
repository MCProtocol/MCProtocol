/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.entities

import com.google.gson.Gson


/**
 * https://wiki.vg/Chat
 * @author Cubxity
 * @since 7/21/2019
 */
data class Message @JvmOverloads constructor(
    var text: String? = null,
    var color: ChatColor? = null,
    var bold: Boolean = false,
    var italic: Boolean = false,
    var underlined: Boolean = false,
    var strikethrough: Boolean = false,
    var obfuscated: Boolean = false,
    var insertion: String? = null,
    var clickEvent: ClickEvent? = null,
    var hoverEvent: HoverEvent? = null,
    var extra: MutableList<Message>? = null,
    var translate: String? = null,
    var score: String? = null,
    var with: MutableList<Any>? = null
) {
    companion object {
        private val gson = Gson()


        @JvmStatic
        fun fromJson(json: String): Message {
            return try {
                gson.fromJson(json, Message::class.java)
            } catch (e: Exception) {
                println(json)
                e.printStackTrace()
                Message("Failed to read")
            }
        }
    }

    data class ClickEvent(
        var action: ClickAction,
        var value: String
    )

    data class HoverEvent(
        var action: HoverAction,
        var value: Any
    )

    fun bold(): Message {
        bold = true
        return this
    }

    fun italic(): Message {
        italic = true
        return this
    }

    fun underline(): Message {
        underlined = true
        return this
    }

    fun strikethrough(): Message {
        strikethrough = true
        return this
    }

    fun obfuscated(): Message {
        obfuscated = true
        return this
    }

    fun toJson(): String = gson.toJson(this)

    fun toText(): String = ((text
        ?: "") + (with?.joinToString(separator = "") { if (it is Message) it.toText() else if (it is String) it else "" }
        ?: "") + (extra?.joinToString(separator = "") { it.text ?: "" } ?: "")).replace("§[0-9a-flnmokr]", "")

    override fun toString(): String {
        return toText()
    }

    fun addExtra(extra: Message) {
        if (this.extra != null)
            this.extra!! += extra
        else this.extra = mutableListOf(extra)
    }
}