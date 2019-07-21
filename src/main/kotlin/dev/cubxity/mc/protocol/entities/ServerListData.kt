package dev.cubxity.mc.protocol.entities

import java.util.*

/**
 * @author Cubxity
 * @since 7/21/2019
 */
data class ServerListData(
    var version: Version,
    val players: MutableList<Player>,
    var description: Message,
    var favicon: String
) {
    data class Version(var name: String, var protocol: Int)

    data class Player(var name: String, var uuid: UUID)
}