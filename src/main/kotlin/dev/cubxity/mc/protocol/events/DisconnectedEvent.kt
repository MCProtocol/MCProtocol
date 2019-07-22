package dev.cubxity.mc.protocol.events

import io.netty.channel.ChannelHandlerContext

/**
 * @author Cubxity
 * @since 7/22/2019
 */
class DisconnectedEvent(val ctx: ChannelHandlerContext) : Event()