package dev.cubxity.mc.protocol.data.obj

import com.github.steveice10.opennbt.tag.builtin.CompoundTag

class Slot(
    var present: Boolean,
    var itemId: Int? = 0,
    var itemCount: Int? = 1,
    var nbt: CompoundTag? = null
)