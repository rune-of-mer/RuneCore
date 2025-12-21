package org.lyralis.runeCore.item.model

import org.bukkit.enchantments.Enchantment

/**
 * エンチャント定義
 *
 * @param enchantment エンチャントの種類
 * @param level エンチャントのレベル
 */
data class CustomItemEnchantment(
    val enchantment: Enchantment,
    val level: Int,
)
