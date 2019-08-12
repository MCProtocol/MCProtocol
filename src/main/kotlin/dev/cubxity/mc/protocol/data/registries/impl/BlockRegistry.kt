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

class BlockRegistry(target: ProtocolVersion) {
    private val gson = Gson()
    private var elements = hashMapOf<Int, BlockEntry>()

    init {
        try {
            val stream = javaClass.getResourceAsStream("/versions/${target.resourceName}/blocks.json")
            val text = stream.bufferedReader().readText()

            val items = gson.fromJson(text, Array<BlockEntry>::class.java).map { it.id to it }.toMap()

            items.forEach {
                for (i in it.value.minStateId..it.value.maxStateId) {
                    elements[i] = it.value
                }
            }
        } catch (e: Exception) {
        }
    }

    fun get(id: Int) = elements[id]

    class BlockEntry(
        val id: Int,
        val displayName: String,
        val name: String,
        val hardness: Double,
        val minStateId: Int,
        val maxStateId: Int,
        val drops: Array<Int>,
        val diggable: Boolean,
        val transparent: Boolean,
        val stackSize: Int
    )

}