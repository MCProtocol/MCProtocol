package dev.cubxity.mc.protocol.events

import reactor.netty.Connection

/**
 * @author Cubxity
 * @since 7/22/2019
 */
class DisconnectedEvent(val connection: Connection) : Event()