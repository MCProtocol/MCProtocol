package dev.cubxity.mc.protocol.packets

import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput

/**
 * This packet does not serialize or deserialize anything
 * @author Cubxity
 * @since 7/21/2019
 */
class PassthroughPacket(val id: Int) : Packet() {
    lateinit var bytes: ByteArray

    override fun read(buf: NetInput) {
        bytes = ByteArray(buf.available())
        buf.readBytes(bytes)
    }

    override fun write(out: NetOutput) {
        out.writeBytes(bytes)
    }
}