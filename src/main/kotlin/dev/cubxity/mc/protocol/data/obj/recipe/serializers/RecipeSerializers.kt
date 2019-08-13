/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.recipe.serializers

import dev.cubxity.mc.protocol.data.obj.Slot
import dev.cubxity.mc.protocol.net.io.NetInput
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

abstract class AbstractRecipeSerializer(var name: String) {
    companion object {
        fun readSerializerData(name: String, buf: NetInput): AbstractRecipeSerializer {
            return when (name) {
                "minecraft:crafting_shapeless" -> ShapelessCraftingRecipeSerializer(
                    buf.readString(),
                    buf.readVarArray { readIngredient(buf) },
                    buf.readSlot()
                )
                "minecraft:crafting_shaped" -> {
                    val width = buf.readVarInt()
                    val height = buf.readVarInt()

                    ShapedCraftingRecipeSerializer(
                        width,
                        height,
                        buf.readString(),
                        ArrayList((0 until (width * height)).map { readIngredient(buf) }),
                        buf.readSlot()
                    )
                }
                "minecraft:smelting", "minecraft:blasting", "minecraft:smoking", "minecraft:campfire_cooking" -> SmeltingRecipeSerializer(
                    name,
                    buf.readString(),
                    readIngredient(buf),
                    buf.readSlot(),
                    buf.readFloat(),
                    buf.readVarInt()
                )
                "minecraft:stonecutting" -> StonecuttingRecipeSerializer(
                    buf.readString(),
                    readIngredient(buf),
                    buf.readSlot()
                )
                else -> EmptyRecipeSerializer(name)
            }
        }
    }

    override fun toString(): String {
        return ReflectionToStringBuilder.toString(this)
    }
}

class ShapelessCraftingRecipeSerializer(var group: String, var ingredients: ArrayList<Ingredient>, var result: Slot) :
    AbstractRecipeSerializer("minecraft:crafting_shapeless")

class ShapedCraftingRecipeSerializer(
    var width: Int,
    var height: Int,
    var group: String,
    var ingredients: ArrayList<Ingredient>,
    var result: Slot
) : AbstractRecipeSerializer("minecraft:crafting_shaped")

class SmeltingRecipeSerializer(
    name: String,
    var group: String,
    var ingredient: Ingredient,
    var result: Slot,
    var experience: Float,
    var cookingTime: Int
) : AbstractRecipeSerializer(name)

class StonecuttingRecipeSerializer(
    var group: String,
    var ingredient: Ingredient,
    var result: Slot
) : AbstractRecipeSerializer("minecraft:stonecutting")

class EmptyRecipeSerializer(name: String) : AbstractRecipeSerializer(name)

private fun readIngredient(buf: NetInput): Ingredient = Ingredient(buf.readVarArray { buf.readSlot() })