package org.lyralis.runeCore.domain.player

import java.time.LocalDateTime
import java.util.UUID

/**
 * プレイヤー設定を示すモデル
 *
 * @param uuid プレイヤーの UUID
 * @param showBossBar ボスバーの表示設定
 * @param updatedAt データの更新日時
 */
data class PlayerSettingsData(
    val uuid: UUID,
    val showBossBar: Boolean = true,
    val updatedAt: LocalDateTime? = null,
)
