package org.lyralis.runeCore.domain.shop

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.item.ItemRegistry

/**
 * ショップで販売されるアイテム定義
 *
 * カテゴリー別にファイルを分けて定義可能
 *
 * @property material 販売するマテリアル（カスタムアイテムの場合はベースマテリアル）
 * @property displayName 日本語の表示名
 * @property price 販売価格 (Rune)
 * @property category 所属カテゴリー
 * @property rarity レア度
 * @property description アイテムの説明（lore として表示）
 * @property bulkPurchaseEnabled 64個まとめ買いを許可するか
 * @property customItemId カスタムアイテムのID（バニラアイテムの場合は null）
 */
interface ShopItem {
    val material: Material
    val displayName: String
    val price: ULong
    val category: ShopCategory
    val rarity: ShopItemRarity
    val description: List<String>
    val bulkPurchaseEnabled: Boolean
    val customItemId: String?

    /**
     * カスタムアイテムかどうか
     */
    val isCustomItem: Boolean
        get() = customItemId != null

    /**
     * 購入時に渡す ItemStack を生成する
     *
     * @param amount 個数
     * @return 生成された ItemStack
     */
    fun createPurchaseItemStack(amount: Int = 1): ItemStack {
        val id = customItemId
        return if (id != null) {
            val customItem = ItemRegistry.getById(id)
            customItem?.createItemStack(amount) ?: ItemStack(material, amount)
        } else {
            ItemStack(material, amount)
        }
    }
}
