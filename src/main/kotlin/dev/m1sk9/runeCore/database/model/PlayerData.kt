package dev.m1sk9.runeCore.database.model

import java.time.LocalDateTime
import java.util.UUID

data class PlayerData(
    val uuid: UUID,
    val level: UInt = 1u,
    val experience: ULong = 0uL,
    val balance: ULong = 0uL,
    val createAt: LocalDateTime? = null,
    val updateAt: LocalDateTime? = null,
)
