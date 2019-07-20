package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.net.PacketEncryption

/**
 * The main juice
 * @author Cubxity
 * @since 7/20/2019
 */
class MCProtocol(val side: Side) {
    var encryption: PacketEncryption? = null
    var compressionThreshold: Int? = null

    /**
     * Applies all default settings
     */
    fun applyDefaults() {

    }

    enum class Side {
        CLIENT,
        SERVER
    }
}