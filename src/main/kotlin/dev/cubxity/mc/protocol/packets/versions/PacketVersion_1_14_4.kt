/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.cubxity.mc.protocol.packets.versions

import dev.cubxity.mc.protocol.packets.Packet
import dev.cubxity.mc.protocol.packets.PacketVersion
import dev.cubxity.mc.protocol.packets.game.client.*
import dev.cubxity.mc.protocol.packets.game.client.player.*
import dev.cubxity.mc.protocol.packets.game.client.player.ClientAnimationPacket
import dev.cubxity.mc.protocol.packets.game.server.*
import dev.cubxity.mc.protocol.packets.game.server.entity.*
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerPlayerAbilitiesPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerPlayerPositionLookPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerSetExperiencePacket
import dev.cubxity.mc.protocol.packets.game.server.entity.player.ServerUpdateHealthPacket
import dev.cubxity.mc.protocol.packets.game.server.entity.spawn.*
import dev.cubxity.mc.protocol.packets.game.server.world.*
import dev.cubxity.mc.protocol.packets.login.client.EncryptionResponsePacket
import dev.cubxity.mc.protocol.packets.login.client.LoginPluginResponsePacket
import dev.cubxity.mc.protocol.packets.login.client.LoginStartPacket
import dev.cubxity.mc.protocol.packets.login.server.*

class PacketVersion_1_14_4 : PacketVersion {

    override val clientPlay: Map<Int, Class<out Packet>> = mapOf(
        0x00 to ClientTeleportConfirmPacket::class.java,
        0x01 to ClientQueryBlockNBTPacket::class.java,
        0x02 to ClientSetDifficultyPacket::class.java,
        0x03 to ClientChatMessagePacket::class.java,
        0x04 to ClientStatusPacket::class.java,
        0x05 to ClientClientSettingsPacket::class.java,
        0x06 to ClientTabCompletePacket::class.java,
        0x07 to ClientConfirmTransactionPacket::class.java,
        0x08 to ClientClickWindowButtonPacket::class.java,
        0x09 to ClientClickWindowPacket::class.java,
        0x0A to ClientCloseWindowPacket::class.java,
        0x0B to ClientPluginMessagePacket::class.java,
        0x0C to ClientEditBookPacket::class.java,
        0x0D to ClientQueryEntityNBTPacket::class.java,
        0x0E to ClientUseEntityPacket::class.java,
        0x0F to ClientKeepAlivePacket::class.java,
        0x10 to ClientLockDifficultyPacket::class.java,
        0x11 to ClientPlayerPositionPacket::class.java,
        0x12 to ClientPlayerPositionAndLookPacket::class.java,
        0x13 to ClientPlayerLookPacket::class.java,
        0x14 to ClientPlayerPacket::class.java,
        0x15 to ClientVehicleMovePacket::class.java,
        0x16 to ClientSteerBoatPacket::class.java,
        0x17 to ClientPickItemPacket::class.java,
        0x18 to ClientCraftRecipeRequestPacket::class.java,
        0x19 to ClientPlayerAbilitiesPacket::class.java,
        0x1A to ClientPlayerDiggingPacket::class.java,
        0x1B to ClientEntityActionPacket::class.java,
        0x1C to ClientSteerVehiclePacket::class.java,
        0x1D to ClientRecipeBookDataPacket::class.java,
        0x1E to ClientNameItemPacket::class.java,
        0x1F to ClientResourcePackStatusPacket::class.java,
        0x20 to ClientAdvancementTabPacket::class.java,
        0x21 to ClientSelectTradePacket::class.java,
        0x22 to ClientSetBeaconEffectPacket::class.java,
        0x23 to ClientHeldItemChangePacket::class.java,
        0x24 to ClientUpdateCommandBlockPacket::class.java,
        0x25 to ClientUpdateCommandMinecartPacket::class.java,
        0x26 to ClientCreativeInventoryActionPacket::class.java,
        0x27 to ClientUpdateJigsawBlockPacket::class.java,
        0x28 to ClientUpdateStructureBlockPacket::class.java,
        0x29 to ClientUpdateSignPacket::class.java,
        0x2A to ClientAnimationPacket::class.java,
        0x2B to ClientSpectatePacket::class.java,
        0x2C to ClientPlayerBlockPlacementPacket::class.java,
        0x2D to ClientUseItemPacket::class.java
    )

    override val serverPlay: Map<Int, Class<out Packet>> = mapOf(
        0x00 to ServerSpawnObjectPacket::class.java,
        0x01 to ServerSpawnExperienceOrbPacket::class.java,
        0x02 to ServerSpawnGlobalEntityPacket::class.java,
        0x03 to ServerSpawnMobPacket::class.java,
        0x04 to ServerSpawnPaintingPacket::class.java,
        0x05 to ServerSpawnPlayerPacket::class.java,
        0x06 to ServerAnimationPacket::class.java,
        0x07 to ServerStatisticsPacket::class.java,
        0x08 to ServerBlockBreakAnimationPacket::class.java,
        0x09 to ServerUpdateBlockEntity::class.java,
        0x0A to ServerBlockActionPacket::class.java,
        0x0B to ServerBlockChangePacket::class.java,
        0x0C to ServerBossBarPacket::class.java,
        0x0D to ServerDifficultyPacket::class.java,
        0x0E to ServerChatPacket::class.java,
        0x0F to ServerMultiBlockChangePacket::class.java,
        0x10 to ServerTabCompletePacket::class.java,
        0x11 to ServerDeclareCommandsPacket::class.java,
        0x12 to ServerConfirmTransactionPacket::class.java,
        0x13 to ServerCloseWindowPacket::class.java,
        0x14 to ServerWindowItemsPacket::class.java,
        0x15 to ServerWindowPropertyPacket::class.java,
        0x16 to ServerSetSlotPacket::class.java,
        0x17 to ServerSetCooldownPacket::class.java,
        0x18 to ServerPluginMessagePacket::class.java,
        0x19 to ServerNamedSoundEffectPacket::class.java,
        0x1A to ServerDisconnectPacket::class.java,
        0x1B to ServerEntityStatusPacket::class.java,
        0x1C to ServerExplosionPacket::class.java,
        0x1D to ServerUnloadChunkPacket::class.java,
        0x1E to ServerChangeGameStatePacket::class.java,
        0x1F to ServerOpenHorseWindowPacket::class.java,
        0x20 to ServerKeepAlivePacket::class.java,
        0x21 to ServerChunkDataPacket::class.java,
        0x22 to ServerEffectPacket::class.java,
        0x23 to ServerParticlePacket::class.java,
        0x24 to ServerUpdateLightPacket::class.java,
        0x25 to ServerJoinGamePacket::class.java,
        0x26 to ServerMapDataPacket::class.java,
        0x27 to ServerTradeListPacket::class.java,
        0x28 to ServerEntityRelativeMovePacket::class.java,
        0x29 to ServerEntityLookAndRelativeMovePacket::class.java,
        0x2A to ServerEntityLookPacket::class.java,
        0x2B to ServerEntityPacket::class.java,
        0x2C to ServerVehicleMovePacket::class.java,
        0x2D to ServerOpenBookPacket::class.java,
        0x2E to ServerOpenWindowPacket::class.java,
        0x2F to ServerOpenSignEditorPacket::class.java,
        0x30 to ServerCraftRecipeResponsePacket::class.java,
        0x31 to ServerPlayerAbilitiesPacket::class.java,
        0x32 to ServerCombatEventPacket::class.java,
        0x33 to ServerPlayerInfoPacket::class.java,
        0x34 to ServerFacePlayerPacket::class.java,
        0x35 to ServerPlayerPositionLookPacket::class.java,
        0x36 to ServerUnlockRecipesPacket::class.java,
        0x37 to ServerDestroyEntitiesPacket::class.java,
        0x38 to ServerRemoveEntityEffectPacket::class.java,
        0x39 to ServerResourcePackSendPacket::class.java,
        0x3A to ServerRespawnPacket::class.java,
        0x3B to ServerEntityHeadLookPacket::class.java,
        0x3C to ServerSelectAdvancementTabPacket::class.java,
        0x3D to ServerWorldBorderPacket::class.java,
        0x3E to ServerCameraPacket::class.java,
        0x3F to ServerHeldItemChangePacket::class.java,
        0x40 to ServerUpdateViewPositionPacket::class.java,
        0x41 to ServerUpdateViewDistancePacket::class.java,
        0x42 to ServerDisplayScoreboardPacket::class.java,
        0x43 to ServerEntityMetadataPacket::class.java,
        0x44 to ServerAttachEntityPacket::class.java,
        0x45 to ServerEntityVelocityPacket::class.java,
        0x46 to ServerEntityEquipmentPacket::class.java,
        0x47 to ServerSetExperiencePacket::class.java,
        0x48 to ServerUpdateHealthPacket::class.java,
        0x49 to ServerScoreboardObjectivePacket::class.java,
        0x4A to ServerSetPassengersPacket::class.java,
        0x4B to ServerTeamsPacket::class.java,
        0x4C to ServerUpdateScorePacket::class.java,
        0x4D to ServerSpawnPositionPacket::class.java,
        0x4E to ServerTimeUpdatePacket::class.java,
        0x4F to ServerTitlePacket::class.java,
        0x50 to ServerEntitySoundEffectPacket::class.java,
        0x51 to ServerSoundEffectPacket::class.java,
        0x52 to ServerStopSoundPacket::class.java,
        0x53 to ServerPlayerListHeaderAndFooterPacket::class.java,
        0x54 to ServerNBTQueryResponsePacket::class.java,
        0x55 to ServerCollectItemPacket::class.java,
        0x56 to ServerEntityTeleportPacket::class.java,
        0x57 to ServerAdvancementsPacket::class.java,
        0x58 to ServerEntityPropertiesPacket::class.java,
        0x59 to ServerEntityEffectPacket::class.java,
        0x5A to ServerDeclareRecipesPacket::class.java,
        0x5B to ServerTagsPacket::class.java
    )

    override val clientLogin: Map<Int, Class<out Packet>> = mapOf(
        0x00 to LoginStartPacket::class.java,
        0x01 to EncryptionResponsePacket::class.java,
        0x02 to LoginPluginResponsePacket::class.java
    )

    override val serverLogin: Map<Int, Class<out Packet>> = mapOf(
        0x00 to LoginDisconnectPacket::class.java,
        0x01 to EncryptionRequestPacket::class.java,
        0x02 to LoginSuccessPacket::class.java,
        0x03 to SetCompressionPacket::class.java,
        0x04 to LoginPluginRequestPacket::class.java
    )
}