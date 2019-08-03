/*
 * Copyright (c) 2018 - 2019 Cubixity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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