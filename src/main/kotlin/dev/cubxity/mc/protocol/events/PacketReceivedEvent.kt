package dev.cubxity.mc.protocol.events

import dev.cubxity.mc.protocol.packets.Packet

/**
 * @author Cubxity
 * @since 7/22/2019
 */
data class PacketReceivedEvent<T : Packet>(val packet: T) : Event()