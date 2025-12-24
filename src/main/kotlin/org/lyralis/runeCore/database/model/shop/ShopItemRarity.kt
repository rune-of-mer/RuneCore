package org.lyralis.runeCore.database.model.shop

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

/**
 * ショップアイテムのレア度定義
 *
 * @param displayName レア度の表示名
 * @param color レア度に対応する色
 */
enum class ShopItemRarity(
    val displayName: String,
    val color: TextColor,
) {
    COMMON("Common", NamedTextColor.WHITE),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.AQUA),
    EPIC("Epic", NamedTextColor.LIGHT_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD),
}
