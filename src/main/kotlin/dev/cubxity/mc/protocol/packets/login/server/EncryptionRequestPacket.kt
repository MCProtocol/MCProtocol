package dev.cubxity.mc.protocol.packets.login.server

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.utils.CryptUtil
import java.security.PublicKey

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class EncryptionRequestPacket(var serverId: String, var publicKey: PublicKey, var verifyToken: ByteArray) : Packet() {
    override fun read(buf: NetInput, target: ProtocolVersion) {
        serverId = buf.readString()
        publicKey = CryptUtil.decodePublicKey(buf.readBytes(buf.readVarInt()))
        verifyToken = buf.readBytes(buf.readVarInt())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeString(serverId)
        val encoded = publicKey.encoded
        out.writeVarInt(encoded.size)
        out.writeBytes(encoded)
        out.writeVarInt(verifyToken.size)
        out.writeBytes(verifyToken)
    }
}