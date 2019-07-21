package dev.cubxity.mc.protocol.dsl

import dev.cubxity.mc.protocol.MCClient
import dev.cubxity.mc.protocol.MCServer
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.ProtocolSession.Side.CLIENT
import dev.cubxity.mc.protocol.ProtocolSession.Side.SERVER
import io.netty.channel.Channel

/**
 * DSL Function to build [ProtocolSession]
 * @param side side that [ProtocolSession] will act like. Possible values are [CLIENT] and [SERVER]
 */
fun buildProtocol(side: ProtocolSession.Side, channel: Channel, block: ProtocolSession.() -> Unit = {}): ProtocolSession = ProtocolSession(side, channel).apply(block)

/**
 * DSL Function to build [ProtocolSession] with default configurations
 */
fun defaultProtocol(side: ProtocolSession.Side, channel: Channel) =
    ProtocolSession(side, channel).apply { applyDefaults() }
/**
 * DSL Function to build [MCClient]
 */
fun client(
    host: String,
    port: Int = 25565,
    sessionFactory: (Channel) -> ProtocolSession = { defaultProtocol(CLIENT, it) },
    block: MCClient.() -> Unit = {}
) = MCClient(host, port, sessionFactory).apply(block)

/**
 * DSL Function to build [MCServer]
 */
fun server(
    host: String = "127.0.0.1",
    port: Int = 25565,
    sessionFactory: (Channel) -> ProtocolSession = { defaultProtocol(SERVER, it) },
    block: MCServer.() -> Unit = {}
) = MCServer(host, port, sessionFactory).apply(block)