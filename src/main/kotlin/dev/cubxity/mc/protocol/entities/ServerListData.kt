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
        fun fromJson(json: String): ServerListData = mapper.readValue(json, ServerListData::class.java)
    }

    data class Version(var name: String, var protocol: Int)

    data class Players @JvmOverloads constructor(
        var max: Int,
        var online: Int,
        val sample: MutableList<Player> = mutableListOf()
    )

    data class Player(var name: String, var uuid: UUID)

    fun toJson(): String = mapper.writeValueAsString(this)
}