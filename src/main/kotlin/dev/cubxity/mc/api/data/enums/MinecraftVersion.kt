/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.api.data.enums

enum class MinecraftVersion(val versionName: String, val protocolVersion: Int) {
    V_1_14_4("1.14.4", 498),
    V_1_14_3("1.14.3", 490),
    V_1_14_2("1.14.2", 485),
    V_1_14_1("1.14.1", 480),
    V_1_14("1.14", 477),
    V_1_13_2("1.13.2", 404),
    V_1_13_1("1.13.1", 401),
    V_1_13("1.13", 393),
    V_1_12_2("1.12.2", 340),
    V_1_12_1("1.12.1", 338),
    V_1_12("1.12", 335),
    V_1_11_2("1.11.2", 316),
    V_1_11_1("1.11.1", 316),
    V_1_11("1.11", 315),
    V_1_10_2("1.10.2", 210),
    V_1_10_1("1.10.1", 210),
    V_1_10("1.10", 210),
    V_1_9_4("1.9.4", 110),
    V_1_9_3("1.9.3", 110),
    V_1_9_2("1.9.2", 109),
    V_1_9_1("1.9.1", 108),
    V_1_9("1.9", 107),
    V_1_8_9("1.8.9", 47),
    V_1_8_8("1.8.8", 47),
    V_1_8_7("1.8.7", 47),
    V_1_8_6("1.8.6", 47),
    V_1_8_5("1.8.5", 47),
    V_1_8_4("1.8.4", 47),
    V_1_8_3("1.8.3", 47),
    V_1_8_2("1.8.2", 47),
    V_1_8_1("1.8.1", 47),
    V_1_8("1.8", 47),
    V_1_7_10("1.7.10", 5),
    V_1_7_9("1.7.9", 5),
    V_1_7_8("1.7.8", 5),
    V_1_7_7("1.7.7", 5),
    V_1_7_6("1.7.6", 5),
    V_1_7_5("1.7.5", 4),
    V_1_7_4("1.7.4", 4),
    V_1_7_2("1.7.2", 4),
    V_1_7_1("1.7.1", 3),
    V_1_7("1.7", 3),
    ;

    companion object {
        val FIRST = V_1_7_5
    }
}