package dev.cubxity.mc.protocol.dsl

import dev.cubxity.mc.protocol.MCClient
import dev.cubxity.mc.protocol.MCProtocol
import dev.cubxity.mc.protocol.MCProtocol.Side.CLIENT
import dev.cubxity.mc.protocol.MCProtocol.Side.SERVER
import dev.cubxity.mc.protocol.MCServer

/**
 * DSL Function to build [MCProtocol]
 * @param side side that [MCProtocol] will act like. Possible values are [CLIENT] and [SERVER]
 */
fun buildProtocol(side: MCProtocol.Side, block: MCProtocol.() -> Unit = {}): MCProtocol = MCProtocol(side).apply(block)

/**
 * DSL Function to build [MCProtocol] with default configurations
 */
fun defaultProtocol(side: MCProtocol.Side) = MCProtocol(side).apply { applyDefaults() }

/**
 * DSL Function to build [MCClient]
 */
fun client(host: String, port: Int = 25565, protocol: MCProtocol = MCProtocol(CLIENT), block: MCClient.() -> Unit = {}) =
    MCClient(host, port, protocol).apply(block)

/**
 * DSL Function to build [MCServer]
 */
fun server(host: String, port: Int = 25565, protocol: MCProtocol = MCProtocol(SERVER), block: MCServer.() -> Unit = {}) =
    MCServer(host, port, protocol).apply(block)