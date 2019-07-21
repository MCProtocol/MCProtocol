package dev.cubxity.mc.protocol.example

import dev.cubxity.mc.protocol.dsl.client

/**
 * @author Cubxity
 * @since 7/20/2019
 */
fun main() {
    client("mc.hypixel.net") {
        bind().subscribe { println("Connected to Hypixel!") }
    }
}