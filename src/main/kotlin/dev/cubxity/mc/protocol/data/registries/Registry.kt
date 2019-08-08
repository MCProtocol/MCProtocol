/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.registries

import com.google.gson.Gson
import com.google.gson.JsonParser
import dev.cubxity.mc.protocol.ProtocolVersion

class Registry(version: ProtocolVersion, val id: String) {

    private val gson = Gson()
    private val parser = JsonParser()

    private val entries = hashMapOf<Int, String>()

    private var defaultEntry: String? = null
//        private set

    init {
        try {
            val stream = javaClass.getResourceAsStream("/versions/${version.simple}/reports/registries.json")
            val text = stream.bufferedReader().readText()

            val parsed = parser.parse(text).asJsonObject

            val obj = parsed[id].asJsonObject
            defaultEntry = if (obj.has("default")) obj["default"].asString else null

            entries.putAll(obj["entries"].asJsonObject.entrySet().map { it.value.asJsonObject["protocol_id"].asInt to it.key })
        } catch (e: Exception) {
        }
    }

    fun getName(id: Int) = entries[id]
    fun getId(name: String) = entries.entries.firstOrNull { it.value.equals(name, true) }?.key

}