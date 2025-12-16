package dev.m1sk9.runeCore.database.model

import java.time.LocalDateTime
import java.util.UUID

data class PlayerStatsData(
    val uuid: UUID,
    val kills: UInt,
    val mobKills: UInt,
    val deaths: UInt,
    val updatedAt: LocalDateTime? = null,
)
