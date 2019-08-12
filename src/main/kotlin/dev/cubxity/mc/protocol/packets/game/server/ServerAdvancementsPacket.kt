/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.game.server


import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.advancements.Advancement
import dev.cubxity.mc.protocol.data.obj.advancements.AdvancementCriterionProgress
import dev.cubxity.mc.protocol.data.obj.advancements.AdvancementDisplay
import dev.cubxity.mc.protocol.data.obj.advancements.AdvancementFrameType
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput
import dev.cubxity.mc.protocol.packets.Packet

class ServerAdvancementsPacket(
    var reset: Boolean,
    var advancements: HashMap<String, Advancement>,
    var toBeRemovedAdvancements: ArrayList<String>,
    var progressMap: HashMap<String, HashMap<String, AdvancementCriterionProgress>>
) : Packet() {

    override fun read(buf: NetInput, target: ProtocolVersion) {
        reset = buf.readBoolean()

        advancements = HashMap()

        for (i in 0 until buf.readVarInt()) {
            advancements[buf.readString()] = readAdvancement(buf)
        }

        val toBeRemovedAdvancementNumber = buf.readVarInt()

        toBeRemovedAdvancements = ArrayList(toBeRemovedAdvancementNumber)

        for (i in 0 until toBeRemovedAdvancementNumber) {
            toBeRemovedAdvancements.add(buf.readString())
        }

        progressMap = HashMap()

        for (i in 0 until buf.readVarInt()) {
            val name = buf.readString()

            val criteriaSize = buf.readVarInt()

            val criteria = HashMap<String, AdvancementCriterionProgress>()

            for (j in 0 until criteriaSize) {
                val name = buf.readString()
                val achieved = buf.readBoolean()

                criteria[name] = AdvancementCriterionProgress(
                    achieved,
                    if (achieved) buf.readLong() else null
                )
            }
            progressMap[name] = criteria
        }

    }


    override fun write(out: NetOutput, target: ProtocolVersion) {
        TODO("Implement write")
    }

    private fun readAdvancement(buf: NetInput): Advancement {
        val parent = if (buf.readBoolean()) buf.readString() else null
        val display = if (buf.readBoolean()) readAdvancementDisplay(buf) else null

        val criteriaNumber = buf.readVarInt()
        val criteria = ArrayList<String>(criteriaNumber)

        for (j in 0 until criteriaNumber) {
            criteria.add(buf.readString())
        }

        val requirementArrayNumber = buf.readVarInt()
        val requirementArrays = ArrayList<ArrayList<String>>(requirementArrayNumber)

        for (j in 0 until requirementArrayNumber) {
            val requirementNumber = buf.readVarInt()
            val requirements = ArrayList<String>(requirementNumber)

            for (f in 0 until requirementNumber) {
                requirements.add(buf.readString())
            }

            requirementArrays.add(requirements)
        }
        val advancement = Advancement(parent, display, criteria, requirementArrays)
        return advancement
    }

    private fun readAdvancementDisplay(buf: NetInput): AdvancementDisplay {
        val title = buf.readMessage()
        val description = buf.readMessage()
        val icon = buf.readSlot()
        val frameType = AdvancementFrameType.values()[buf.readVarInt()]
        val flags = buf.readInt()
        val backgroundTexture = if ((flags and 0x1) != 0) buf.readString() else null
        val x = buf.readFloat()
        val y = buf.readFloat()

        return AdvancementDisplay(
            title,
            description,
            icon,
            frameType,
            (flags and 0x2) != 0,
            (flags and 0x4) != 0,
            backgroundTexture,
            x,
            y
        )
    }

}
