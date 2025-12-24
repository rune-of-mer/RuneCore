package org.lyralis.runeCore.database.model.gacha

import java.util.UUID

/**
 * プレイヤーのガチャ天井カウントのデータモデル
 *
 * @param playerUuid プレイヤーのUUID
 * @param eventId ガチャイベントID
 * @param pullCount 現在のガチャ回数
 */
data class PlayerGachaPityData(
    val playerUuid: UUID,
    val eventId: String,
    val pullCount: UInt,
)
