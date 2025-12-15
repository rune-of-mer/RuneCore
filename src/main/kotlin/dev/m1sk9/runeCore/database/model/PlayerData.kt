package dev.m1sk9.runeCore.database.model

import java.time.LocalDateTime

data class PlayerData(
    val uuid: String,
    val level: UInt = 1u,
    val experience: ULong = 0uL,
    val balance: ULong = 0uL,
    val createAt: LocalDateTime? = null,
    val updateAt: LocalDateTime? = null,
)
