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

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.auth.exception.request.InvalidCredentialsException
import com.github.steveice10.mc.auth.exception.request.RequestException
import com.github.steveice10.mc.auth.exception.request.ServiceUnavailableException
import com.github.steveice10.mc.auth.service.AuthenticationService
import com.github.steveice10.mc.auth.service.SessionService
import dev.cubxity.mc.protocol.data.magic.Dimension
import dev.cubxity.mc.protocol.data.magic.Gamemode
import dev.cubxity.mc.protocol.data.magic.LevelType
import dev.cubxity.mc.protocol.dsl.msg
import dev.cubxity.mc.protocol.entities.ServerListData
import dev.cubxity.mc.protocol.events.*
import dev.cubxity.mc.protocol.net.PacketEncryption
import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.RawPacket
import dev.cubxity.mc.protocol.packets.game.client.ClientChatMessagePacket
import dev.cubxity.mc.protocol.packets.game.client.ClientKeepAlivePacket
import dev.cubxity.mc.protocol.packets.game.client.ClientStatusPacket
import dev.cubxity.mc.protocol.packets.game.client.ClientTeleportConfirmPacket
import dev.cubxity.mc.protocol.packets.game.client.player.ClientPlayerDiggingPacket
import dev.cubxity.mc.protocol.packets.game.client.player.ClientPlayerPacket
import dev.cubxity.mc.protocol.packets.game.client.player.ClientPlayerPositionLookPacket
import dev.cubxity.mc.protocol.packets.game.server.*
import dev.cubxity.mc.protocol.packets.game.server.entity.*
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerPlayerAbilitiesPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerPlayerPositionLookPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerSetExperiencePacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerUpdateHealthPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.*
import dev.cubxity.mc.protocol.packets.game.server.world.*
import dev.cubxity.mc.protocol.packets.handshake.client.HandshakePacket
import dev.cubxity.mc.protocol.packets.login.client.EncryptionResponsePacket
import dev.cubxity.mc.protocol.packets.login.client.LoginStartPacket
import dev.cubxity.mc.protocol.packets.login.server.EncryptionRequestPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginDisconnectPacket
import dev.cubxity.mc.protocol.packets.login.server.LoginSuccessPacket
import dev.cubxity.mc.protocol.packets.login.server.SetCompressionPacket
import dev.cubxity.mc.protocol.packets.status.client.StatusPingPacket
import dev.cubxity.mc.protocol.packets.status.client.StatusQueryPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusPongPacket
import dev.cubxity.mc.protocol.packets.status.server.StatusResponsePacket
import dev.cubxity.mc.protocol.state.Tracker
import dev.cubxity.mc.protocol.utils.CryptUtil
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.nio.NioSocketChannel
import kotlinx.coroutines.*
import org.objenesis.ObjenesisStd
import org.slf4j.LoggerFactory
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers
import reactor.netty.Connection
import java.math.BigInteger
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The main juice
 * @author Cubxity
 * @since 7/20/2019
 */
class ProtocolSession @JvmOverloads constructor(
    val side: Side,
    val connection: Connection,
    val channel: NioSocketChannel,
    var incomingVersion: ProtocolVersion = ProtocolVersion.V1_14_4,
    var outgoingVersion: ProtocolVersion = ProtocolVersion.V1_14_4
) : SimpleChannelInboundHandler<Packet>(), CoroutineScope {

    override val coroutineContext = Dispatchers.Default + Job()

    private val objenesis = ObjenesisStd()

    val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Packet encryption
     * https://wiki.vg/Protocol_Encryption
     */
    var encryption: PacketEncryption? = null

    /**
     * Protocol compression
     * https://wiki.vg/Protocol#With_compression
     */
    var compressionThreshold: Int = 256

    /**
     * If the compression should be enabled,
     * this should be enabled ONLY after received/sent the [SetCompressionPacket]
     */
    var enableCompression: Boolean = false

    /**
     * Intent for the client
     */
    var intent: HandshakePacket.Intent = HandshakePacket.Intent.LOGIN

    /**
     * If the server and client default handler should skip auth
     */
    var offline: Boolean = false

    /**
     * Session username
     */
    lateinit var username: String

    /**
     * Session profile
     */
    lateinit var profile: GameProfile

    /**
     * Session access token
     */
    var accessToken: String? = null

    /**
     * Session client token
     */
    var clientToken: String = UUID.randomUUID().toString()

    var keyPair = CryptUtil.generateKeyPair()

    /**
     * Map of registered incoming packets
     */
    val incomingPackets = mutableMapOf<Int, Class<out Packet>>()

    /**
     * Map of registered outgoing packets
     */
    val outgoingPackets = mutableMapOf<Int, Class<out Packet>>()

    /**
     * Sub protocol state
     * - https://wiki.vg/Protocol#Handshaking
     * - https://wiki.vg/Protocol#Status
     * - https://wiki.vg/Protocol#Login
     * - https://wiki.vg/Protocol#Play
     */
    var subProtocol = SubProtocol.HANDSHAKE

    val processor = EmitterProcessor.create<Event>()
    val scheduler = Schedulers.newSingle("Protocol-PacketManager", true)
    val sink = processor.sink(FluxSink.OverflowStrategy.BUFFER)

    /**
     * Do not use this unless it's required
     * The listeners will be called in the thread that [channelRead0] is called from
     * @see on
     */
    val syncListeners = CopyOnWriteArrayList<(Packet) -> Unit>()

    /**
     * Applies all default settings
     */
    fun applyDefaults() {
        registerDefaults()
        when (side) {
            Side.CLIENT -> defaultClientHandler()
            Side.SERVER -> defaultServerHandler()
        }
    }

    fun defaultServerHandler() {
        val verifyToken = ByteArray(4)
        Random().nextBytes(verifyToken)
        syncListeners += {
            when (it) {
                is HandshakePacket -> {
                    subProtocol = when (it.intent) {
                        HandshakePacket.Intent.LOGIN -> SubProtocol.LOGIN
                        HandshakePacket.Intent.STATUS -> SubProtocol.STATUS
                    }
                    registerDefaults()
                }
                is LoginStartPacket -> {
                    username = it.username
                    send(EncryptionRequestPacket("", keyPair.public, verifyToken))
                }
                is EncryptionResponsePacket -> {
                    if (!Arrays.equals(it.getVerifyToken(keyPair.private), verifyToken)) {
                        disconnect("Invalid nonce")
                    } else
                        if (offline) {
                            encryption = PacketEncryption(it.getSecretKey(keyPair.private))
                        } else {
                            launch {
                                try {
                                    val profile = SessionService().getProfileByServer(
                                        username,
                                        BigInteger(
                                            CryptUtil.getServerIdHash(
                                                "",
                                                keyPair.public,
                                                it.getSecretKey(keyPair.private)
                                            )
                                        ).toString(16)
                                    )
                                    if (profile == null)
                                        disconnect("Failed to verify username.")
                                    else {
                                        this@ProtocolSession.profile = profile
                                        send(LoginSuccessPacket(profile.id.toString(), profile.name))
                                            ?.addListener {
                                                subProtocol = SubProtocol.GAME
                                                registerDefaults()

                                                send(
                                                    ServerJoinGamePacket(
                                                        0,
                                                        Gamemode.CREATIVE,
                                                        false,
                                                        Dimension.OVERWORLD,
                                                        1,
                                                        LevelType.DEFAULT,
                                                        8
                                                    )
                                                )
                                                GlobalScope.launch {
                                                    var lastPing: Long
                                                    while (channel.isOpen) {
                                                        lastPing = System.currentTimeMillis()
                                                        send(ServerKeepAlivePacket(lastPing))
                                                        delay(2000)
                                                    }
                                                }
                                            }
                                    }
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    disconnect("Failed to authenticate")
                                }
                            }
                        }
                }
            }
        }
        on<PacketReceivedEvent>()
            .filter { it.packet is StatusQueryPacket }
            .next()
            .subscribe {
                send(
                    StatusResponsePacket(
                        ServerListData(
                            ServerListData.Version("MCProtocol", outgoingVersion.id),
                            msg("MCProtocol Server"),
                            ServerListData.Players(1, 0)
                        )
                    )
                )
            }
        on<PacketReceivedEvent>()
            .filter { it.packet is StatusPingPacket }
            .map { it.packet as StatusPingPacket }
//            .next()
            .subscribe {
                send(StatusPongPacket(it.time))
            }
    }

    fun defaultClientHandler() {
        on<ConnectedEvent>()
            .next()
            .subscribe {
                val addr = channel.remoteAddress()
                send(
                    HandshakePacket(
                        outgoingVersion.id,
                        addr.hostString,
                        addr.port,
                        intent
                    )
                )
                    ?.addListener {
                        subProtocol = when (intent) {
                            HandshakePacket.Intent.LOGIN -> SubProtocol.LOGIN
                            HandshakePacket.Intent.STATUS -> SubProtocol.STATUS
                        }
                        registerDefaults()
                        send(LoginStartPacket(profile.name))
                    }
            }
        on<PacketReceivedEvent>()
            .filter { it.packet is EncryptionRequestPacket }
            .map { it.packet as EncryptionRequestPacket }
            .next()
            .subscribe {
                val key = CryptUtil.generateSharedKey()
                val serverHash =
                    BigInteger(CryptUtil.getServerIdHash(it.serverId, it.publicKey, key)).toString(16)
                try {
                    if (accessToken != null) {
                        SessionService().joinServer(profile, accessToken, serverHash)
                    }

                    send(EncryptionResponsePacket(key, it.publicKey, it.verifyToken))
                    encryption = PacketEncryption(key)
                } catch (e: ServiceUnavailableException) {
                    disconnect("Login failed: Authentication service unavailable.")
                } catch (e: InvalidCredentialsException) {
                    disconnect("Login failed: Invalid login session.")
                } catch (e: RequestException) {
                    disconnect("Login failed: Authentication error: " + e.message)
                }
            }
        on<PacketReceivedEvent>()
            .filter { it.packet is ServerKeepAlivePacket }
            .map { it.packet as ServerKeepAlivePacket }
            .subscribe {
                send(ClientKeepAlivePacket(it.time))
            }
        syncListeners += {
            when (it) {
                is LoginSuccessPacket -> {
                    subProtocol = SubProtocol.GAME
                    registerDefaults()
                }
                is SetCompressionPacket -> {
                    compressionThreshold = it.threshold
                    enableCompression = true
                    logger.debug("Set compression: ${it.threshold}")
                }
            }
        }
    }

    /**
     * Registers default packets for current [subProtocol]
     * @param clear to clear current registered packets in [incomingPackets] and [outgoingPackets]
     */
    @JvmOverloads
    fun registerDefaults(clear: Boolean = true) {
        if (clear) {
            incomingPackets.clear()
            outgoingPackets.clear()
        }
        // from client
        val client = when (side) {
            Side.CLIENT -> outgoingPackets
            Side.SERVER -> incomingPackets
        }
        // from server
        val server = when (side) {
            Side.CLIENT -> incomingPackets
            Side.SERVER -> outgoingPackets
        }

        when (subProtocol) {
            SubProtocol.HANDSHAKE -> client[0x00] = HandshakePacket::class.java
            SubProtocol.STATUS -> {
                server[0x00] = StatusResponsePacket::class.java
                server[0x01] = StatusPongPacket::class.java

                client[0x00] = StatusQueryPacket::class.java
                client[0x01] = StatusPingPacket::class.java
            }

            SubProtocol.LOGIN -> {
                server.putAll(incomingVersion.version.serverLogin)
                client.putAll(outgoingVersion.version.clientLogin)
            }

            SubProtocol.GAME -> {
                server[0x00] = ServerSpawnObjectPacket::class.java
                server[0x01] = ServerSpawnExperienceOrbPacket::class.java
                server[0x02] = ServerSpawnGlobalEntityPacket::class.java
                server[0x03] = ServerSpawnMobPacket::class.java
                server[0x04] = ServerSpawnPaintingPacket::class.java
                server[0x05] = ServerSpawnPlayerPacket::class.java
                server[0x06] = ServerAnimationPacket::class.java
                server[0x07] = ServerStatisticsPacket::class.java
                server[0x08] = ServerBlockBreakAnimationPacket::class.java
                server[0x09] = ServerUpdateBlockEntity::class.java
                server[0x0A] = ServerBlockActionPacket::class.java
                server[0x0B] = ServerBlockChangePacket::class.java
                server[0x0C] = ServerBossBarPacket::class.java
                server[0x0D] = ServerDifficultyPacket::class.java
                server[0x0E] = ServerChatPacket::class.java
                server[0x0F] = ServerMultiBlockChangePacket::class.java
                server[0x10] = ServerTabCompletePacket::class.java
                server[0x11] = ServerDeclareCommandsPacket::class.java
                server[0x12] = ServerConfirmTransactionPacket::class.java
                server[0x13] = ServerCloseWindowPacket::class.java
                server[0x14] = ServerWindowItemsPacket::class.java
                server[0x15] = ServerWindowPropertyPacket::class.java
                server[0x16] = ServerSetSlotPacket::class.java
                server[0x17] = ServerSetCooldownPacket::class.java
                server[0x18] = ServerPluginMessagePacket::class.java
                server[0x19] = ServerNamedSoundEffectPacket::class.java
                server[0x1A] = ServerDisconnectPacket::class.java
                server[0x1B] = ServerEntityStatusPacket::class.java
                server[0x1C] = ServerExplosionPacket::class.java
                server[0x1D] = ServerUnloadChunkPacket::class.java
                server[0x1E] = ServerChangeGameStatePacket::class.java
                server[0x1F] = ServerOpenHorseWindowPacket::class.java
                server[0x20] = ServerKeepAlivePacket::class.java
                server[0x21] = ServerChunkDataPacket::class.java
                server[0x22] = ServerEffectPacket::class.java
                server[0x23] = ServerParticlePacket::class.java
                server[0x24] = ServerUpdateLightPacket::class.java
                server[0x25] = ServerJoinGamePacket::class.java
                // TODO Implement 0x26: Map Data
                // TODO Implement 0x27: Trade List
                server[0x28] = ServerEntityRelativeMovePacket::class.java
                server[0x29] = ServerEntityLookAndRelativeMovePacket::class.java
                server[0x2A] = ServerEntityLookPacket::class.java
                server[0x2B] = ServerEntityPacket::class.java
                server[0x2C] = ServerVehicleMovePacket::class.java
                server[0x2D] = ServerOpenBookPacket::class.java
                server[0x2E] = ServerOpenWindowPacket::class.java
                server[0x2F] = ServerOpenSignEditorPacket::class.java
                server[0x30] = ServerCraftRecipeResponsePacket::class.java
                server[0x31] = ServerPlayerAbilitiesPacket::class.java
                server[0x32] = ServerCombatEventPacket::class.java
                server[0x33] = ServerPlayerInfoPacket::class.java
                server[0x34] = ServerFacePlayerPacket::class.java
                server[0x35] = ServerPlayerPositionLookPacket::class.java
                server[0x36] = ServerUnlockRecipesPacket::class.java
                server[0x37] = ServerDestroyEntitiesPacket::class.java
                server[0x38] = ServerRemoveEntityEffectPacket::class.java
                server[0x39] = ServerResourcePackSendPacket::class.java
                server[0x3A] = ServerRespawnPacket::class.java
                server[0x3B] = ServerEntityHeadLookPacket::class.java
                server[0x3C] = ServerSelectAdvancementTabPacket::class.java
                server[0x3D] = ServerWorldBorderPacket::class.java
                server[0x3E] = ServerCameraPacket::class.java
                server[0x3F] = ServerHeldItemChangePacket::class.java
                server[0x40] = ServerUpdateViewPositionPacket::class.java
                server[0x41] = ServerUpdateViewDistancePacket::class.java
                server[0x42] = ServerDisplayScoreboardPacket::class.java
                server[0x43] = ServerEntityMetadataPacket::class.java
                server[0x44] = ServerAttachEntityPacket::class.java
                server[0x45] = ServerEntityVelocityPacket::class.java
                server[0x46] = ServerEntityEquipmentPacket::class.java
                server[0x47] = ServerSetExperiencePacket::class.java
                server[0x48] = ServerUpdateHealthPacket::class.java
                server[0x49] = ServerScoreboardObjectivePacket::class.java
                server[0x4A] = ServerSetPassengersPacket::class.java
                // TODO Implement 0x4B: Teams
                server[0x4C] = ServerUpdateScorePacket::class.java
                server[0x4D] = ServerSpawnPositionPacket::class.java
                server[0x4E] = ServerTimeUpdatePacket::class.java
                server[0x4F] = ServerTitlePacket::class.java
                server[0x50] = ServerEntitySoundEffectPacket::class.java
                server[0x51] = ServerSoundEffectPacket::class.java
                server[0x52] = ServerStopSoundPacket::class.java
                server[0x53] = ServerPlayerListHeaderAndFooterPacket::class.java
                server[0x54] = ServerNBTQueryResponsePacket::class.java
                server[0x55] = ServerCollectItemPacket::class.java
                server[0x56] = ServerEntityTeleportPacket::class.java
                // TODO Implement 0x57: Advancements
                server[0x58] = ServerEntityPropertiesPacket::class.java
                server[0x59] = ServerEntityEffectPacket::class.java
                // TODO Implement 0x5A: Declare Recipes
                // TODO Implement 0x5B: Tags

                client[0x00] = ClientTeleportConfirmPacket::class.java
                client[0x03] = ClientChatMessagePacket::class.java
                client[0x04] = ClientStatusPacket::class.java

                client[0x0F] = ClientKeepAlivePacket::class.java
                client[0x12] = ClientPlayerPositionLookPacket::class.java
                client[0x1A] = ClientPlayerDiggingPacket::class.java
                client[0x14] = ClientPlayerPacket::class.java

                server.putAll(incomingVersion.version.serverPlay)
                client.putAll(outgoingVersion.version.clientPlay)
            }
        }
    }

    /**
     * Offline minecraft user
     * @param username
     * @param uuid optional UUID
     */
    @JvmOverloads
    fun offline(username: String, uuid: UUID = UUID.randomUUID()) {
        profile = GameProfile(uuid, username)
    }

    /**
     * Logging into Minecraft
     * @param username email of Mojang account or username for legacy account
     * @param using username or access/refresh token depending on [token] parameter
     * @param clientToken optional client token
     */
    @JvmOverloads
    fun login(username: String, using: String, clientToken: String = this.clientToken, token: Boolean = false) {
        val auth = AuthenticationService(clientToken)
        auth.username = username
        if (token)
            auth.accessToken = using
        else
            auth.password = using
        auth.login()
        profile = auth.selectedProfile
        accessToken = auth.accessToken
        this.clientToken = auth.clientToken
    }

    /**
     * Prints every packet received
     * This is used for debugging
     * NOTE: [logger]'s level is required to be at DEBUG
     */
    fun wiretap(): ProtocolSession {
        on<PacketReceivedEvent>()
            .subscribe { (packet) -> logger.debug("[$side - RECEIVED]: ${if (packet is RawPacket) "RawPacket id: ${packet.id}" else "$packet"}") }
        on<PacketSentEvent>()
            .subscribe { (packet) -> logger.debug("[$side - SENT]: ${packet.javaClass.simpleName}") }
        return this
    }

    fun tracker() = Tracker(this)

    inline fun <reified T : Event> on() =
        processor.publishOn(scheduler)
            .ofType(T::class.java)

    fun createOutgoingPacketById(id: Int): Packet {
        val p = outgoingPackets[id] ?: return RawPacket(id)
        return objenesis
            .getInstantiatorOf(p)
            .newInstance()
    }

    fun createIncomingPacketById(id: Int): Packet {
        val p = incomingPackets[id] ?: return RawPacket(id)
        return objenesis
            .getInstantiatorOf(p)
            .newInstance()
    }

    fun getOutgoingId(packet: Packet) =
        outgoingPackets.keys.elementAtOrElse(outgoingPackets.values.indexOf(packet::class.java)) { (packet as? RawPacket)?.id }

    fun getIncomingId(packet: Packet) =
        incomingPackets.keys.elementAtOrElse(incomingPackets.values.indexOf(packet::class.java)) { (packet as? RawPacket)?.id }

    fun send(packet: Packet): ChannelFuture? {
        val e = PacketSendingEvent(packet)
        sink.next(e)
        //TODO: Fix the issue with this being non-blocking
        if (!e.isCancelled)
            return channel.writeAndFlush(packet)
                .addListener { sink.next(PacketSentEvent(packet)) }
        return null
    }

    fun disconnect(reason: String) {
        logger.debug("Disconnected: $reason")
        if (channel.isOpen) {
            val m = msg(reason)
            send(if (subProtocol == SubProtocol.LOGIN) LoginDisconnectPacket(m) else ServerDisconnectPacket(m))
                ?.addListener { channel.disconnect() }
        }
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        syncListeners.forEach { it(packet) }
        sink.next(PacketReceivedEvent(packet))
    }

    enum class Side {
        CLIENT,
        SERVER
    }

    enum class SubProtocol {
        HANDSHAKE,
        STATUS,
        LOGIN,
        GAME
    }
}
