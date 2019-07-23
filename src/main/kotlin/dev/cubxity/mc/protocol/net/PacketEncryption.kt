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

    fun decrypt(input: ByteArray, inputOffset: Int, inputLength: Int, output: ByteArray, outputOffset: Int) =
        inCipher.update(input, inputOffset, inputLength, output, outputOffset)

    fun encrypt(input: ByteArray, inputOffset: Int, inputLength: Int, output: ByteArray, outputOffset: Int) =
        this.outCipher.update(input, inputOffset, inputLength, output, outputOffset)

    fun getEncryptedSize(len: Int) = outCipher.getOutputSize(len)

    fun getDecryptedSize(len: Int) = inCipher.getOutputSize(len)
}