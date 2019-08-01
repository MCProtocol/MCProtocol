package dev.cubxity.mc.protocol.data.magic

import dev.cubxity.mc.protocol.ProtocolVersion

object MagicRegistry {

    val versionData = hashMapOf<ProtocolVersion, MutableMap<Any, Any>>()

    init {
        ProtocolVersion.values().forEach { registerVersion(it) }
    }

    private fun registerVersion(version: ProtocolVersion) {
        val data = mutableMapOf<Any, Any>()

        data[GlobalEntityType.THUNDER_BOLT] = 0

        data[ObjectType.BOAT] = 1
        data[ObjectType.ITEM_STACK] = 2
        data[ObjectType.AREA_EFFECT_CLOUD] = 3
        data[ObjectType.MINECART] = 10
        data[ObjectType.ACTIVATED_TNT] = 50
        data[ObjectType.ENDER_CRYSTAL] = 51
        data[ObjectType.ARROW] = 60
        data[ObjectType.SNOWBALL] = 61
        data[ObjectType.EGG] = 62
        data[ObjectType.FIRE_BALL] = 63
        data[ObjectType.FIRE_CHARGE] = 64
        data[ObjectType.THROWN_ENDERPEARL] = 65
        data[ObjectType.WITHER_SKULL] = 66
        data[ObjectType.SHULKER_BULLET] = 67
        data[ObjectType.LLAMA_SPIT] = 68
        data[ObjectType.FALLING_OBJECT] = 70
        data[ObjectType.ITEM_FRAME] = 71
        data[ObjectType.EYE_OF_ENDER] = 72
        data[ObjectType.THROWN_POTION] = 73
        data[ObjectType.THROWN_EXP_BOTTLE] = 75
        data[ObjectType.FIREWORK_ROCKET] = 76
        data[ObjectType.LEASH_KNOT] = 77
        data[ObjectType.ARMOR_STAND] = 78
        data[ObjectType.EVOCATION_FANGS] = 79
        data[ObjectType.FISHING_HOOK] = 90
        data[ObjectType.SPECTRAL_ARROW] = 91
        data[ObjectType.DRAGON_FIREBALL] = 93
        data[ObjectType.TRIDENT] = 94

        data[MessageType.CHAT] = 0
        data[MessageType.SYSTEM] = 1
        data[MessageType.NOTIFICATION] = 2

        data[Gamemode.SURVIVAL] = 0
        data[Gamemode.CREATIVE] = 1
        data[Gamemode.ADVENTURE] = 2
        data[Gamemode.SPECTATOR] = 3

        data[Dimension.NETHER] = -1
        data[Dimension.OVERWORLD] = 0
        data[Dimension.END] = 1

        data[Difficulity.PEACEFUL] = 0
        data[Difficulity.EASY] = 1
        data[Difficulity.NORMAL] = 2
        data[Difficulity.HARD] = 3

        data[LevelType.DEFAULT] = "default"
        data[LevelType.FLAT] = "flat"
        data[LevelType.LARGE_BIOMES] = "largeBiomes"
        data[LevelType.AMPLIFIED] = "amplified"
        data[LevelType.DEFAULT_1_1] = "default_1_1"

        for (i in MobType.values().indices) {
            val enum = MobType.values()[i]
            val value = if (enum == MobType.SLIME) 67 else i
            data[enum] = value
        }

        for (i in PaintingType.values().indices)
            data[PaintingType.values()[i]] = i

        for (i in Direction.values().indices)
            data[Direction.values()[i]] = i

        for (i in MetadataType.values().indices)
            data[MetadataType.values()[i]] = i

        for (i in Pose.values().indices)
            data[Pose.values()[i]] = i

        versionData[version] = data
    }

    inline fun <reified T> lookupKey(version: ProtocolVersion, value: Any): T {
        val data = versionData[version]!!
        return data.entries.find { it.value == value && it.key is T }!!.key as T
    }

    inline fun <reified T> lookupValue(version: ProtocolVersion, key: Any) = versionData[version]!![key] as T
}