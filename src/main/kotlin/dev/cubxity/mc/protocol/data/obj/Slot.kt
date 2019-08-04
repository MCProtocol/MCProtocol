/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.obj

import com.github.steveice10.opennbt.tag.builtin.Tag

class Slot {
    var isPresent: Boolean = false
        private set
    var itemId: Int? = null
    var count: Int? = null
    var nbt: Tag? = null

    constructor(itemId: Int, count: Int, nbt: Tag) {
        this.isPresent = true
        this.itemId = itemId
        this.count = count
        this.nbt = nbt
    }

    constructor() {
        this.isPresent = false
    }

    override fun toString(): String {
        return if (isPresent) {
            "Slot{" +
                    "present=" + isPresent +
                    ", itemId=" + itemId +
                    ", count=" + count +
                    ", nbt=" + nbt +
                    '}'.toString()
        } else {
            "Slot{" +
                    "present=" + isPresent +
                    '}'.toString()
        }
    }
}
