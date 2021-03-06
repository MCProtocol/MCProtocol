/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dev.cubxity.mc.protocol.data.obj.commandData.parser

abstract class AbstractParser protected constructor(val name: String)

/*
 * Not implemented
 */
class SimpleParser(name: String) : AbstractParser(name)

/*
 * Brigadier-Parsers
 */
class DoubleParser @JvmOverloads constructor(var min: Double = Double.MIN_VALUE, var max: Double = Double.MIN_VALUE) :
    AbstractParser("brigadier:double")

class FloatParser @JvmOverloads constructor(var min: Float = Float.MIN_VALUE, var max: Float = Float.MIN_VALUE) :
    AbstractParser("brigadier:float")

class IntegerParser @JvmOverloads constructor(var min: Int = Int.MIN_VALUE, var max: Int = Int.MIN_VALUE) :
    AbstractParser("brigadier:integer")

class StringParser(val type: Type) : AbstractParser("brigadier:string") {
    enum class Type {
        SINGLE_WORD,
        QUOTABLE_PHRASE,
        GREEDY_PHRASE
    }
}

/*
 * Minecraft-Parsers
 */
class EntityParser(val flags: Int) : AbstractParser("minecraft:entity") {
    companion object {
        val ALLOW_ONLY_SINGLE = 0x01
        val ALLOW_ONLY_PLAYERS = 0x02
    }
}

class ScoreHolderParser constructor(var allowsMultiple: Boolean) : AbstractParser("minecraft:score_holder")
class RangeParser constructor(var allowsDecimal: Boolean) : AbstractParser("minecraft:range")
