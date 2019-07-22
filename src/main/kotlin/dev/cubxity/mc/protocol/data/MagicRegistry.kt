package dev.cubxity.mc.protocol.data

import dev.cubxity.mc.protocol.ProtocolVersion
import dev.cubxity.mc.protocol.data.enum.*

object MagicRegistry {

    val versionData = hashMapOf<ProtocolVersion, MutableMap<Any, Any>>()

    init {
        ProtocolVersion.values().forEach { registerVersion(it) }
    }

    private fun registerVersion(version: ProtocolVersion) {
        val data = mutableMapOf<Any, Any>()

        data[EnumGlobalEntityType.THUNDER_BOLT] = 1

        data[EnumObjectType.BOAT] = 1
        data[EnumObjectType.ITEM_STACK] = 2
        data[EnumObjectType.AREA_EFFECT_CLOUD] = 3
        data[EnumObjectType.MINECART] = 10
        data[EnumObjectType.ACTIVATED_TNT] = 50
        data[EnumObjectType.ENDER_CRYSTAL] = 51
        data[EnumObjectType.ARROW] = 60
        data[EnumObjectType.SNOWBALL] = 61
        data[EnumObjectType.EGG] = 62
        data[EnumObjectType.FIRE_BALL] = 63
        data[EnumObjectType.FIRE_CHARGE] = 64
        data[EnumObjectType.THROWN_ENDERPEARL] = 65
        data[EnumObjectType.WITHER_SKULL] = 66
        data[EnumObjectType.SHULKER_BULLET] = 67
        data[EnumObjectType.LLAMA_SPIT] = 68
        data[EnumObjectType.FALLING_OBJECT] = 70
        data[EnumObjectType.ITEM_FRAME] = 71
        data[EnumObjectType.EYE_OF_ENDER] = 72
        data[EnumObjectType.THROWN_POTION] = 73
        data[EnumObjectType.THROWN_EXP_BOTTLE] = 75
        data[EnumObjectType.FIREWORK_ROCKET] = 76
        data[EnumObjectType.LEASH_KNOT] = 77
        data[EnumObjectType.ARMOR_STAND] = 78
        data[EnumObjectType.EVOCATION_FANGS] = 79
        data[EnumObjectType.FISHING_HOOK] = 90
        data[EnumObjectType.SPECTRAL_ARROW] = 91
        data[EnumObjectType.DRAGON_FIREBALL] = 93
        data[EnumObjectType.TRIDENT] = 94

        for (i in EnumMobType.values().indices) {
            val enum = EnumMobType.values()[i]
            val value = if (enum == EnumMobType.SLIME) 67 else i
            data[enum] = value
        }

        for (i in EnumPaintingType.values().indices)
            data[EnumMobType.values()[i]] = i

        for (i in EnumDirection.values().indices)
            data[EnumDirection.values()[i]] = i

        versionData[version] = data
    }

    inline fun <reified T> lookupKey(version: ProtocolVersion, value: Any): T {
        val data = versionData[version]!!
        return data.keys.elementAt(data.values.indexOf(value)) as T
    }

    inline fun <reified T> lookupValue(version: ProtocolVersion, key: Any) = versionData[version]!![key] as T
}