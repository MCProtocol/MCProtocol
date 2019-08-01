package dev.cubxity.mc.protocol.packets

interface PacketVersion {

    val clientPlay: Map<Int, Class<out Packet>>
    val serverPlay: Map<Int, Class<out Packet>>
    val clientLogin: Map<Int, Class<out Packet>>
    val serverLogin: Map<Int, Class<out Packet>>

}