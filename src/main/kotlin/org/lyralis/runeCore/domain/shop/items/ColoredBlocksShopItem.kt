package org.lyralis.runeCore.domain.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.domain.shop.ShopCategory
import org.lyralis.runeCore.domain.shop.ShopItem
import org.lyralis.runeCore.domain.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * 色別ブロックカテゴリーのアイテム定義
 */
enum class ColoredBlocksShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    // 羊毛
    WHITE_WOOL(Material.WHITE_WOOL, "白色の羊毛", 10uL, ShopItemRarity.COMMON),
    ORANGE_WOOL(Material.ORANGE_WOOL, "橙色の羊毛", 10uL, ShopItemRarity.COMMON),
    MAGENTA_WOOL(Material.MAGENTA_WOOL, "赤紫色の羊毛", 10uL, ShopItemRarity.COMMON),
    LIGHT_BLUE_WOOL(Material.LIGHT_BLUE_WOOL, "空色の羊毛", 10uL, ShopItemRarity.COMMON),
    YELLOW_WOOL(Material.YELLOW_WOOL, "黄色の羊毛", 10uL, ShopItemRarity.COMMON),
    LIME_WOOL(Material.LIME_WOOL, "黄緑色の羊毛", 10uL, ShopItemRarity.COMMON),
    PINK_WOOL(Material.PINK_WOOL, "桃色の羊毛", 10uL, ShopItemRarity.COMMON),
    GRAY_WOOL(Material.GRAY_WOOL, "灰色の羊毛", 10uL, ShopItemRarity.COMMON),
    LIGHT_GRAY_WOOL(Material.LIGHT_GRAY_WOOL, "薄灰色の羊毛", 10uL, ShopItemRarity.COMMON),
    CYAN_WOOL(Material.CYAN_WOOL, "青緑色の羊毛", 10uL, ShopItemRarity.COMMON),
    PURPLE_WOOL(Material.PURPLE_WOOL, "紫色の羊毛", 10uL, ShopItemRarity.COMMON),
    BLUE_WOOL(Material.BLUE_WOOL, "青色の羊毛", 10uL, ShopItemRarity.COMMON),
    BROWN_WOOL(Material.BROWN_WOOL, "茶色の羊毛", 10uL, ShopItemRarity.COMMON),
    GREEN_WOOL(Material.GREEN_WOOL, "緑色の羊毛", 10uL, ShopItemRarity.COMMON),
    RED_WOOL(Material.RED_WOOL, "赤色の羊毛", 10uL, ShopItemRarity.COMMON),
    BLACK_WOOL(Material.BLACK_WOOL, "黒色の羊毛", 10uL, ShopItemRarity.COMMON),

    // テラコッタ
    TERRACOTTA(Material.TERRACOTTA, "テラコッタ", 8uL, ShopItemRarity.COMMON),
    WHITE_TERRACOTTA(Material.WHITE_TERRACOTTA, "白色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    ORANGE_TERRACOTTA(Material.ORANGE_TERRACOTTA, "橙色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    MAGENTA_TERRACOTTA(Material.MAGENTA_TERRACOTTA, "赤紫色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    LIGHT_BLUE_TERRACOTTA(Material.LIGHT_BLUE_TERRACOTTA, "空色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    YELLOW_TERRACOTTA(Material.YELLOW_TERRACOTTA, "黄色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    LIME_TERRACOTTA(Material.LIME_TERRACOTTA, "黄緑色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    PINK_TERRACOTTA(Material.PINK_TERRACOTTA, "桃色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    GRAY_TERRACOTTA(Material.GRAY_TERRACOTTA, "灰色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    LIGHT_GRAY_TERRACOTTA(Material.LIGHT_GRAY_TERRACOTTA, "薄灰色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    CYAN_TERRACOTTA(Material.CYAN_TERRACOTTA, "青緑色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    PURPLE_TERRACOTTA(Material.PURPLE_TERRACOTTA, "紫色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    BLUE_TERRACOTTA(Material.BLUE_TERRACOTTA, "青色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    BROWN_TERRACOTTA(Material.BROWN_TERRACOTTA, "茶色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    GREEN_TERRACOTTA(Material.GREEN_TERRACOTTA, "緑色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    RED_TERRACOTTA(Material.RED_TERRACOTTA, "赤色のテラコッタ", 10uL, ShopItemRarity.COMMON),
    BLACK_TERRACOTTA(Material.BLACK_TERRACOTTA, "黒色のテラコッタ", 10uL, ShopItemRarity.COMMON),

    // ガラス
    GLASS(Material.GLASS, "ガラス", 8uL, ShopItemRarity.COMMON),

    // ろうそく
    CANDLE(Material.CANDLE, "ろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    WHITE_CANDLE(Material.WHITE_CANDLE, "白色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    ORANGE_CANDLE(Material.ORANGE_CANDLE, "橙色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    MAGENTA_CANDLE(Material.MAGENTA_CANDLE, "赤紫色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    LIGHT_BLUE_CANDLE(Material.LIGHT_BLUE_CANDLE, "空色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    YELLOW_CANDLE(Material.YELLOW_CANDLE, "黄色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    LIME_CANDLE(Material.LIME_CANDLE, "黄緑色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    PINK_CANDLE(Material.PINK_CANDLE, "桃色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    GRAY_CANDLE(Material.GRAY_CANDLE, "灰色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    LIGHT_GRAY_CANDLE(Material.LIGHT_GRAY_CANDLE, "薄灰色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    CYAN_CANDLE(Material.CYAN_CANDLE, "青緑色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    PURPLE_CANDLE(Material.PURPLE_CANDLE, "紫色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    BLUE_CANDLE(Material.BLUE_CANDLE, "青色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    BROWN_CANDLE(Material.BROWN_CANDLE, "茶色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    GREEN_CANDLE(Material.GREEN_CANDLE, "緑色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    RED_CANDLE(Material.RED_CANDLE, "赤色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),
    BLACK_CANDLE(Material.BLACK_CANDLE, "黒色のろうそく", 25uL, ShopItemRarity.UNCOMMON, listOf(), false),

    // 旗
    WHITE_BANNER(Material.WHITE_BANNER, "白色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    ORANGE_BANNER(Material.ORANGE_BANNER, "橙色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    MAGENTA_BANNER(Material.MAGENTA_BANNER, "赤紫色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    LIGHT_BLUE_BANNER(Material.LIGHT_BLUE_BANNER, "空色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    YELLOW_BANNER(Material.YELLOW_BANNER, "黄色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    LIME_BANNER(Material.LIME_BANNER, "黄緑色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    PINK_BANNER(Material.PINK_BANNER, "桃色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    GRAY_BANNER(Material.GRAY_BANNER, "灰色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    LIGHT_GRAY_BANNER(Material.LIGHT_GRAY_BANNER, "薄灰色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    CYAN_BANNER(Material.CYAN_BANNER, "青緑色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    PURPLE_BANNER(Material.PURPLE_BANNER, "紫色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    BLUE_BANNER(Material.BLUE_BANNER, "青色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    BROWN_BANNER(Material.BROWN_BANNER, "茶色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    GREEN_BANNER(Material.GREEN_BANNER, "緑色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    RED_BANNER(Material.RED_BANNER, "赤色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    BLACK_BANNER(Material.BLACK_BANNER, "黒色の旗", 30uL, ShopItemRarity.UNCOMMON, listOf(), false),
    ;

    override val category: ShopCategory = ShopCategory.COLORED_BLOCKS

    override fun createPurchaseItemStack(amount: Int): ItemStack {
        val id = customItemId
        return if (id != null) {
            val customItem = ItemRegistry.getById(id)
            customItem?.createItemStack(amount) ?: ItemStack(material, amount)
        } else {
            ItemStack(material, amount)
        }
    }

    companion object {
        fun all(): List<ShopItem> = entries.toList()
    }
}
