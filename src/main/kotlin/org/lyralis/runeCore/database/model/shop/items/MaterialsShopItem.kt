package org.lyralis.runeCore.database.model.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.model.shop.ShopCategory
import org.lyralis.runeCore.database.model.shop.ShopItem
import org.lyralis.runeCore.database.model.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * 素材カテゴリーのアイテム定義
 */
enum class MaterialsShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    CHARCOAL(Material.CHARCOAL, "木炭", 25uL, ShopItemRarity.UNCOMMON),
    SUGAR(Material.SUGAR, "砂糖", 8uL, ShopItemRarity.COMMON),
    SNOWBALL(Material.SNOWBALL, "雪玉", 5uL, ShopItemRarity.COMMON),
    BONE(Material.BONE, "骨", 12uL, ShopItemRarity.COMMON),
    ;

    override val category: ShopCategory = ShopCategory.MATERIALS

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
