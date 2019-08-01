package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.packets.PacketVersion
import dev.cubxity.mc.protocol.packets.versions.PacketVersion_1_14_4

/**
 * @author Cubxity
 * @since 7/20/2019
 */
enum class ProtocolVersion(val id: Int, val version: PacketVersion) {
    V1_8(48, PacketVersion_1_14_4()),
    V1_9(107, PacketVersion_1_14_4()),
    V1_10(210, PacketVersion_1_14_4()),
    V1_13_2(404, PacketVersion_1_14_4()),
    V1_14_4(498, PacketVersion_1_14_4()),
}