package org.lyralis.runeCore.domain.player

import java.time.LocalDateTime
import java.util.UUID

/**
 * プレイヤーデータを示すモデル
 *
 * @param uuid プレイヤーの UUID
 * @param kills プレイヤーのキル数
 * @param mobKills プレイヤーのモブのキル数
 * @param deaths プレイヤーのデス数
 * @param updatedAt データの更新日時
 */
data class PlayerStatsData(
    val uuid: UUID,
    val kills: UInt,
    val mobKills: UInt,
    val deaths: UInt,
    val updatedAt: LocalDateTime? = null,
)
