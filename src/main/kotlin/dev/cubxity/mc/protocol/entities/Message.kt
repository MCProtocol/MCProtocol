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
    var score: String? = null
) {
    companion object {
        private val mapper = jacksonObjectMapper()

        fun fromJson(json: String) = mapper.readValue(json, this::class.java)
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

    fun toJson() = mapper.writeValueAsString(this)

    fun addExtra(extra: Message) {
        if (this.extra != null)
            this.extra!! += extra
        else this.extra = mutableListOf(extra)
    }
}