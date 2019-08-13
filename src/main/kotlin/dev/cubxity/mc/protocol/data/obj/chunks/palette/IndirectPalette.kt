/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj.chunks.palette

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.obj.chunks.BlockState
import dev.cubxity.mc.protocol.data.obj.chunks.util.BlockUtil
import dev.cubxity.mc.protocol.net.io.NetInput
import dev.cubxity.mc.protocol.net.io.NetOutput

class IndirectPalette(private var bpb: Byte, target: ProtocolVersion) : Palette(target) {

    private var idToState = hashMapOf<Int, BlockState>()
    private var stateToId = hashMapOf<BlockState, Int>()

    override fun getIdForState(state: BlockState) = stateToId[state] ?: 0
    override fun getStateForId(id: Int) = idToState[id] ?: BlockState(0)
    override fun getBitsPerBlock() = bpb

    override fun read(data: NetInput) {
        val length = data.readVarInt()
        for (id in 0 until length) {
            val stateId = data.readVarInt()
            val state = BlockUtil.getStateFromGlobalPaletteID(stateId, target)
            idToState[id] = state
            stateToId[state] = id
        }
    }

    override fun write(data: NetOutput) {
        data.writeVarInt(idToState.size)
        for (id in 0 until idToState.size) {
            val state = idToState[id]
            val stateId = BlockUtil.getGlobalPaletteIDFromState(state ?: continue)
            data.writeVarInt(stateId)
        }
    }
}