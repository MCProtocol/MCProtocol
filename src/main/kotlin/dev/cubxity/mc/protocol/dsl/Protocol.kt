package dev.cubxity.mc.protocol.dsl

import dev.cubxity.mc.protocol.MCClient
import dev.cubxity.mc.protocol.MCServer
import dev.cubxity.mc.protocol.ProtocolSession
import dev.cubxity.mc.protocol.ProtocolSession.Side.CLIENT
import dev.cubxity.mc.protocol.ProtocolSession.Side.SERVER
import io.netty.channel.socket.nio.NioSocketChannel
import reactor.netty.Connection

/**
 * DSL Function to build [ProtocolSession]
 * @param side side that [ProtocolSession] will act like. Possible values are [CLIENT] and [SERVER]
 */
fun buildProtocol(
    side: ProtocolSession.Side,
    connection: Connection,
    channel: NioSocketChannel,
    block: ProtocolSession.() -> Unit = {}
): ProtocolSession = ProtocolSession(side, connection, channel).apply(block)

/**
 * DSL Function to build [ProtocolSession] with default configurations
 */
fun defaultProtocol(side: ProtocolSession.Side, connection: Connection, channel: NioSocketChannel) =
    ProtocolSession(side, connection, channel).apply { applyDefaults() }

/**
 * DSL Function to build [MCClient]
 */
fun client(
    host: String,
    port: Int = 25565,
    sessionFactory: (Connection, NioSocketChannel) -> ProtocolSession = { con, ch -> defaultProtocol(CLIENT, con, ch) },
    block: MCClient.() -> Unit = {}
) = MCClient(host, port, sessionFactory).apply(block)

/**
 * DSL Function to build [MCServer]
 */
fun server(
    host: String = "127.0.0.1",
    port: Int = 25565,
    sessionFactory: (Connection, NioSocketChannel) -> ProtocolSession = { con, ch -> defaultProtocol(SERVER, con, ch) },
    block: MCServer.() -> Unit = {}
) = MCServer(host, port, sessionFactory).apply(block)