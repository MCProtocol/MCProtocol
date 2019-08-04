/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
