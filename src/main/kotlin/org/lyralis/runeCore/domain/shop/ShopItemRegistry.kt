package org.lyralis.runeCore.domain.shop

import org.bukkit.Material
import org.lyralis.runeCore.domain.shop.items.BuildingBlocksShopItem
import org.lyralis.runeCore.domain.shop.items.ColoredBlocksShopItem
import org.lyralis.runeCore.domain.shop.items.CustomShopItem
import org.lyralis.runeCore.domain.shop.items.DecorativeBlocksShopItem
import org.lyralis.runeCore.domain.shop.items.FoodShopItem
import org.lyralis.runeCore.domain.shop.items.MaterialsShopItem
import org.lyralis.runeCore.domain.shop.items.NaturalBlocksShopItem
import org.lyralis.runeCore.domain.shop.items.RedstoneBlocksShopItem
import org.lyralis.runeCore.domain.shop.items.ToolsUtilitiesShopItem

/**
 * ショップアイテムのレジストリ
 *
 * 全カテゴリーのアイテムを一元管理し、検索・取得機能を提供する
 */
object ShopItemRegistry {
    /**
     * 全てのショップアイテム
     */
    private val allItems: List<ShopItem> by lazy {
        listOf(
            BuildingBlocksShopItem.all(),
            ColoredBlocksShopItem.all(),
            DecorativeBlocksShopItem.all(),
            NaturalBlocksShopItem.all(),
            RedstoneBlocksShopItem.all(),
            ToolsUtilitiesShopItem.all(),
            FoodShopItem.all(),
            MaterialsShopItem.all(),
            CustomShopItem.all(),
        ).flatten()
    }

    /**
     * Material から ShopItem へのマップ
     */
    private val byMaterial: Map<Material, ShopItem> by lazy {
        allItems.associateBy { it.material }
    }

    /**
     * Material から ShopItem を取得する
     *
     * @param material マテリアル
     * @return ShopItem．未定義の場合は null
     */
    fun fromMaterial(material: Material): ShopItem? = byMaterial[material]

    /**
     * カテゴリーでフィルタリングする
     *
     * @param category フィルタするカテゴリ
     * @return 該当する ShopItem のリスト
     */
    fun byCategory(category: ShopCategory): List<ShopItem> = allItems.filter { it.category == category }

    /**
     * アイテム名で検索する（部分一致、大文字小文字無視）
     * 日本語の displayName と Material 名の両方で検索可能
     *
     * @param query 検索クエリ
     * @return 該当する ShopItem のリスト
     */
    fun search(query: String): List<ShopItem> {
        val lowerQuery = query.lowercase()
        return allItems.filter { item ->
            item.displayName.contains(query) ||
                item.material.name
                    .lowercase()
                    .contains(lowerQuery)
        }
    }

    /**
     * 全アイテムを取得する
     *
     * @return 全ての ShopItem のリスト
     */
    fun all(): List<ShopItem> = allItems
}
