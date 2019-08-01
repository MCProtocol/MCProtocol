package dev.cubxity.mc.protocol.data.obj

import dev.cubxity.mc.protocol.data.magic.MetadataType

data class EntityMetadata(
    val id: Int,
    val type: MetadataType,
    val value: Any?
)