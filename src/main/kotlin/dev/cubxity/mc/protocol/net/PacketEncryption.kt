package dev.cubxity.mc.protocol.net

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

/**
 * @author Cubxity
 * @since 7/20/2019
 */
class PacketEncryption(val key: Key) {
    val inCipher = Cipher.getInstance("AES/CFB8/NoPadding").apply { init(2, key, IvParameterSpec(key.encoded)) }!!
    val outCipher = Cipher.getInstance("AES/CFB8/NoPadding").apply { init(1, key, IvParameterSpec(key.encoded)) }!!

    fun encrypt(input: ByteArray, output: ByteArray) = outCipher.update(input, 0, input.size, output)

    fun decrypt(input: ByteArray, output: ByteArray) = inCipher.update(input, 0, input.size, output)

    fun getEncryptedSize(len: Int) = outCipher.getOutputSize(len)

    fun getDecryptedSize(len: Int) = inCipher.getOutputSize(len)
}