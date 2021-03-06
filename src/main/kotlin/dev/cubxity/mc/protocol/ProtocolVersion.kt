/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol

import dev.cubxity.mc.protocol.data.registries.RegistryManager
import dev.cubxity.mc.protocol.packets.PacketVersion
import dev.cubxity.mc.protocol.packets.versions.PacketVersion_1_14_4

/**
 * @author Cubxity
 * @since 7/20/2019
 */
enum class ProtocolVersion(val id: Int, val simple: String, val resourceName: String, val version: PacketVersion) {
    V1_8(48, "1.8.9", "1_8_9", PacketVersion_1_14_4()),
    V1_9(107, "1.9", "1_9", PacketVersion_1_14_4()),
    V1_10(210, "1.10.2", "1_10_2", PacketVersion_1_14_4()),
    V1_13_2(404, "1.13.2", "1_13_2", PacketVersion_1_14_4()),
    V1_14_4(498, "1.14.1_14_4", "1_14_4", PacketVersion_1_14_4());

    val registryManager = RegistryManager(this)
}