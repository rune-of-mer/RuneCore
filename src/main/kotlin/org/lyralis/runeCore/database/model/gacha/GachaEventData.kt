package org.lyralis.runeCore.database.model.gacha

/**
 * ガチャイベントのデータモデル
 *
 * @param id イベントの一意な識別子
 * @param displayName 表示名
 * @param ticketCost 1回に必要なチケット枚数
 * @param isActive アクティブかどうか
 * @param pityThreshold 天井に達するまでの回数
 */
data class GachaEventData(
    val id: String,
    val displayName: String,
    val ticketCost: UInt,
    val isActive: Boolean,
    val pityThreshold: UInt,
)
