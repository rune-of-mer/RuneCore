package org.lyralis.runeCore.database.model.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.model.shop.ShopCategory
import org.lyralis.runeCore.database.model.shop.ShopItem
import org.lyralis.runeCore.database.model.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * カスタムアイテムカテゴリーのアイテム定義
 */
enum class CustomShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    // 例:
    // DEBUG_COMPASS(
    //     material = Material.COMPASS,
    //     displayName = "ゲームモード切り替えコンパス",
    //     price = 10000uL,
    //     rarity = ShopItemRarity.LEGENDARY,
    //     description = listOf("右クリック + スニーク: ゲームモード切り替え"),
    //     bulkPurchaseEnabled = false,
    //     customItemId = "debug_compass",
    // ),
    ;

    override val category: ShopCategory = ShopCategory.CUSTOM_ITEMS

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
