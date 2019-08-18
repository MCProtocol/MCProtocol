/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.api

import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.events.Event
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.state.Tracker
import io.netty.channel.ChannelFuture
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import java.util.*

interface IProtocol {
    var clientToken: String
    val processor: EmitterProcessor<Event>
    val scheduler: Scheduler

    fun applyDefaults()
    fun offline(username: String, uuid: UUID = UUID.randomUUID())
    fun login(username: String, using: String, clientToken: String, token: Boolean = false)
    fun wiretap(filter: (Packet) -> Boolean = { true }): ProtocolSession

    fun createTracker(): Tracker
    fun send(packet: Packet): ChannelFuture?
    fun disconnect(reason: String)
}

inline fun <reified T : Event> IProtocol.on(): Flux<T> =
    processor.publishOn(scheduler)
        .ofType(T::class.java)
