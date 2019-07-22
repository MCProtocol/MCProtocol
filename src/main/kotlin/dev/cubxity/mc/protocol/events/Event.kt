package dev.cubxity.mc.protocol.events

open class Event

open class CancellableEvent : Event() {
    var isCancelled: Boolean = false
}