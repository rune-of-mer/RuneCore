package org.lyralis.runeCore.database.model.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.model.shop.ShopCategory
import org.lyralis.runeCore.database.model.shop.ShopItem
import org.lyralis.runeCore.database.model.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * 食べ物カテゴリーのアイテム定義
 */
enum class FoodShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    APPLE(Material.APPLE, "リンゴ", 10uL, ShopItemRarity.COMMON),
    CARROT(Material.CARROT, "ニンジン", 8uL, ShopItemRarity.COMMON),
    POTATO(Material.POTATO, "ジャガイモ", 8uL, ShopItemRarity.COMMON),
    BREAD(Material.BREAD, "パン", 12uL, ShopItemRarity.COMMON),
    ;

    override val category: ShopCategory = ShopCategory.FOOD

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
