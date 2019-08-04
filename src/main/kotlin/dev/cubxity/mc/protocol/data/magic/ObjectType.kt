/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.data.magic

enum class ObjectType(val bbXZ: Double, val bbY: Double) {

    BOAT(1.375, 0.6),
    ITEM_STACK(0.25, 0.25),
    AREA_EFFECT_CLOUD(2.0, 0.5),
    MINECART(0.98, 0.7),
    ACTIVATED_TNT(0.98, 0.98),
    ENDER_CRYSTAL(2.0, 2.0),
    ARROW(0.5, 0.5),
    SNOWBALL(0.25, 0.25),
    EGG(0.25, 0.25),
    FIRE_BALL(1.0, 1.0),
    FIRE_CHARGE(0.3125, 0.3125),
    THROWN_ENDERPEARL(0.25, 0.25),
    WITHER_SKULL(0.3125, 0.3125),
    SHULKER_BULLET(0.3125, 0.3125),
    LLAMA_SPIT(0.25, 0.25),
    FALLING_OBJECT(0.98, 0.98),
    ITEM_FRAME(0.75, 0.75),
    EYE_OF_ENDER(0.25, 0.25),
    THROWN_POTION(0.25, 0.25),
    THROWN_EXP_BOTTLE(0.25, 0.25),
    FIREWORK_ROCKET(0.25, 0.25),
    LEASH_KNOT(0.375, 0.5),
    ARMOR_STAND( 0.5, 1.975),
    EVOCATION_FANGS(0.5, 0.8),
    FISHING_HOOK(0.25, 0.25),
    SPECTRAL_ARROW(0.5, 0.5),
    DRAGON_FIREBALL( 1.0, 1.0),
    TRIDENT(0.0, 0.0)

}