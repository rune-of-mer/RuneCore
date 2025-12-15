package dev.m1sk9.runeCore.database.model

import java.time.LocalDateTime

data class PlayerStatsData(
    val uuid: String,
    val kills: UInt,
    val deaths: UInt,
    val updatedAt: LocalDateTime? = null,
)
