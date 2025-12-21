package org.lyralis.runeCore.item

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

/**
 * カスタムアイテムのレアリティ定義
 *
 * 将来のガチャシステムで排出率の基準として使用される．
 *
 * @param displayName 表示名
 * @param color アイテム名に適用される色
 * @param weight ガチャでの重み（大きいほど出やすい）
 */
enum class ItemRarity(
    val displayName: String,
    val color: TextColor,
    val weight: Int,
) {
    // 通常プレイ/ガチャで入手可能
    COMMON("Common", NamedTextColor.WHITE, 100),
    UNCOMMON("Uncommon", NamedTextColor.GREEN, 50),
    RARE("Rare", NamedTextColor.AQUA, 20),
    EPIC("Epic", NamedTextColor.LIGHT_PURPLE, 8),

    // 特殊コンテンツ/ガチャで入手可能
    LEGENDARY("Legendary", NamedTextColor.GOLD, 3),

    // 課金要素で入手可能
    MYTHIC("Mythic", NamedTextColor.RED, 0),

    // 入手不可
    ADMIN("Admin", NamedTextColor.DARK_RED, 0),
}
