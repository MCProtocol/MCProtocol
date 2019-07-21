package dev.cubxity.mc.protocol.packets.login.client

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.net.NetInput
import dev.cubxity.mc.protocol.net.NetOutput
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.utils.CryptUtil
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey

/**
 * @author Cubxity
 * @since 7/21/2019
 */
class EncryptionResponsePacket(secretKey: SecretKey, publicKey: PublicKey, verifyToken: ByteArray) : Packet() {
    var sharedKey = CryptUtil.encryptData(publicKey, secretKey.encoded)
    var verifyToken = CryptUtil.encryptData(publicKey, verifyToken)

    fun getSecretKey(privateKey: PrivateKey) = CryptUtil.decryptSharedKey(privateKey, sharedKey)

    fun getVerifyToken(privateKey: PrivateKey) = CryptUtil.decryptData(privateKey, verifyToken)

    override fun read(buf: NetInput, target: ProtocolVersion) {
        this.sharedKey = buf.readBytes(buf.readVarInt())
        this.verifyToken = buf.readBytes(buf.readVarInt())
    }

    override fun write(out: NetOutput, target: ProtocolVersion) {
        out.writeVarInt(sharedKey.size)
        out.writeBytes(sharedKey)
        out.writeVarInt(verifyToken.size)
        out.writeBytes(verifyToken)
    }
}