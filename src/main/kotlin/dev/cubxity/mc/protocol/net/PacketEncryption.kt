/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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