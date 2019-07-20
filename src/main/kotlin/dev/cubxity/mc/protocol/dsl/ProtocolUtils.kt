package dev.cubxity.mc.protocol.dsl

import dev.cubxity.mc.protocol.MCProtocol

/**
 * DSL Function to build [MCProtocol]
 * @param block to configure [MCProtocol]
 * @return instance of [MCProtocol]
 */
fun buildProtocol(block: MCProtocol.() -> Unit): MCProtocol = MCProtocol().apply(block)