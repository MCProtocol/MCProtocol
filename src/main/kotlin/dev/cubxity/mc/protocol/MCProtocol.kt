package dev.cubxity.mc.protocol

/**
 * The main juice
 * @author Cubxity
 * @since 7/20/2019
 */
class MCProtocol(val side: Side) {
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