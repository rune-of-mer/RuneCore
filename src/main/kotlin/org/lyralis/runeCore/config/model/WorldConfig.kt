package org.lyralis.runeCore.config.model

data class WorldConfig(
    val life: LifeWorld,
    val resource: ResourceWorld,
    val resourceNether: ResourceNetherWorld,
    val resourceEnd: ResourceEndWorld,
    val dz: DarkZoneWorld,
    val pvp: PvPWorld,
)

data class LifeWorld(
    val name: String,
    val crossWorldCost: Int,
)

data class ResourceWorld(
    val name: String,
    val crossWorldCost: Int,
)

data class ResourceNetherWorld(
    val name: String,
    val crossWorldCost: Int,
)

data class ResourceEndWorld(
    val name: String,
    val crossWorldCost: Int,
)

data class DarkZoneWorld(
    val name: String,
    val crossWorldCost: Int,
    val enabled: Boolean,
    val maxMemberSize: Int,
)

data class PvPWorld(
    val name: String,
    val crossWorldCost: Int,
)
