package dev.cubxity.mc.protocol.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

/**
 * @author Cubxity
 * @since 7/21/2019
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServerListData @JvmOverloads constructor(
    var version: Version,
    var description: Message,
    val players: Players,
    var favicon: String? = null
) {
    companion object {
        private val mapper = jacksonObjectMapper()

        @JvmStatic
        fun fromJson(json: String) = mapper.readValue(json, ServerListData::class.java)
    }

    data class Version(var name: String, var protocol: Int)

    data class Players @JvmOverloads constructor(
        var max: Int,
        var online: Int,
        val sample: MutableList<Player> = mutableListOf()
    )

    data class Player(var name: String, var uuid: UUID)

    fun toJson() = mapper.writeValueAsString(this)
}