package dev.cubxity.mc.protocol.packets

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput

/**
 * @author Cubxity
 * @since 7/20/2019
 */
abstract class Packet {
    /**
     * This method has the responsibility to write the contents of this packet to [buf]
     */
    abstract fun read(buf: NetInput, target: ProtocolVersion)

    /**
     * This method has the responsibility to deserialize [out] into this packet
     */
    abstract fun write(out: NetOutput, target: ProtocolVersion)
}