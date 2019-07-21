package dev.cubxity.mc.protocol.dsl

import dev.cubxity.mc.protocol.entities.Message

fun msg(text: String) = Message(text)

operator fun Message.plus(extra: Message) = addExtra(extra)

operator fun Message.plusAssign(extra: Message) {
    addExtra(extra)
}