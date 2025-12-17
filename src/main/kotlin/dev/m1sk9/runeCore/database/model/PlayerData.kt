package dev.m1sk9.runeCore.database.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * プレイヤーデータを示すモデル
 *
 * @param uuid プレイヤーの UUID
 * @param level プレイヤーのレベル
 * @param experience プレイヤーの経験値
 * @param balance プレイヤーの所持金
 * @param createdAt データの作成日時
 * @param updatedAt データの更新日時
 */
data class PlayerData(
    val uuid: UUID,
    val level: UInt = 1u,
    val experience: ULong = 0uL,
    val balance: ULong = 0uL,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
