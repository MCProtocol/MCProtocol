/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.registries.impl

import com.google.gson.Gson
import dev.cubxity.mc.protocol.ProtocolVersion

class ItemRegistry(target: ProtocolVersion) {
    private val gson = Gson()
    private var elements = hashMapOf<Int, ItemEntry>()

    init {
        try {
            val stream = javaClass.getResourceAsStream("/versions/${target.resourceName}/items.json")
            val text = stream.bufferedReader().readText()

            elements.putAll(gson.fromJson(text, Array<ItemEntry>::class.java).map { it.id to it }.toMap())
        } catch (e: Exception) {
        }
    }

    fun get(id: Int) = elements[id]

    class ItemEntry(
        val id: Int,
        val displayName: String,
        val name: String,
        val stackSize: Int
    )

}