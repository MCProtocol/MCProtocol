/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.entities

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * https://wiki.vg/Chat
 * @author Cubxity
 * @since 7/21/2019
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Message @JvmOverloads constructor(
    var text: String? = null,
    var color: String? = null,
    var bold: String? = null,
    var italic: String? = null,
    var underlined: String? = null,
    var strikethrough: String? = null,
    var obfuscated: String? = null,
    var insertion: String? = null,
    var clickEvent: ClickEvent? = null,
    var hoverEvent: HoverEvent? = null,
    var extra: MutableList<Message>? = null,
    var translate: String? = null,
    var score: String? = null,
    var with: MutableList<Message>? = null
) {
    companion object {
        private val mapper = jacksonObjectMapper()

        @JvmStatic
        fun fromJson(json: String) = mapper.readValue(json, Message::class.java)
    }

    data class ClickEvent @JvmOverloads constructor(
        @JsonAlias("open_url")
        var openUrl: String? = null,
        @JsonAlias("run_command")
        var runCommand: String? = null,
        @Deprecated("No longer supported from 1.9 and above")
        @JsonAlias("twitch_user_info")
        var twitchUserInfo: String? = null,
        @JsonAlias("suggest_command")
        var suggestCommand: String? = null,
        @JsonAlias("change_page")
        var changePage: Int? = null
    )

    data class HoverEvent @JvmOverloads constructor(
        @JsonAlias("show_text")
        var showText: String? = null,
        @JsonAlias("show_item")
        var showItem: String? = null,
        @JsonAlias("show_entity")
        var showEntity: String? = null,
        @Deprecated("No longer supported from 1.12 and above")
        @JsonAlias("show_achievement")
        var showAchievement: String? = null
    )

    fun bold(): Message {
        bold = "true"
        return this
    }

    fun italic(): Message {
        italic = "true"
        return this
    }

    fun underline(): Message {
        underlined = "true"
        return this
    }

    fun strikethrough(): Message {
        strikethrough = "true"
        return this
    }

    fun obfuscated(): Message {
        obfuscated = "true"
        return this
    }

    fun toJson() = mapper.writeValueAsString(this)

    fun toText(): String = ((text ?: "") + (with?.joinToString(separator = "") { it.text ?: "" }
        ?: "") + (extra?.joinToString(separator = "") { it.text ?: "" } ?: "")).replace("§[0-9a-flnmokr]", "")

    fun addExtra(extra: Message) {
        if (this.extra != null)
            this.extra!! += extra
        else this.extra = mutableListOf(extra)
    }
}