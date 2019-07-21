package dev.cubxity.mc.protocol.utils

import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * @author Steveice10
 * @author Cubxity
 */
object CryptUtil {
    fun generateSharedKey(): SecretKey {
        val gen = KeyGenerator.getInstance("AES")
        gen.init(128)
        return gen.generateKey()
    }

    fun generateKeyPair(): KeyPair {
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(1024)
        return gen.generateKeyPair()
    }


    fun decodePublicKey(bytes: ByteArray): PublicKey {
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(bytes))
    }

    fun decryptSharedKey(privateKey: PrivateKey, sharedKey: ByteArray): SecretKey {
        return SecretKeySpec(decryptData(privateKey, sharedKey), "AES")
    }

    fun encryptData(key: Key, data: ByteArray): ByteArray {
        return runEncryption(Cipher.ENCRYPT_MODE, key, data)
    }

    fun decryptData(key: Key, data: ByteArray): ByteArray {
        return runEncryption(Cipher.DECRYPT_MODE, key, data)
    }

    private fun runEncryption(mode: Int, key: Key, data: ByteArray): ByteArray {
        val cipher =
            Cipher.getInstance(if (key.algorithm == "RSA") "RSA/ECB/PKCS1Padding" else "AES/CFB8/NoPadding")
        cipher.init(mode, key)
        return cipher.doFinal(data)
    }

    fun getServerIdHash(serverId: String, publicKey: PublicKey, secretKey: SecretKey): ByteArray {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update(serverId.toByteArray(charset("ISO_8859_1")))
        digest.update(secretKey.encoded)
        digest.update(publicKey.encoded)
        return digest.digest()
    }
}
