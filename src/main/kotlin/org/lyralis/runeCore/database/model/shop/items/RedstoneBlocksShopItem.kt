package org.lyralis.runeCore.database.model.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.model.shop.ShopCategory
import org.lyralis.runeCore.database.model.shop.ShopItem
import org.lyralis.runeCore.database.model.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * レッドストーン関連アイテムカテゴリーのアイテム定義
 */
enum class RedstoneBlocksShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    REDSTONE(Material.REDSTONE, "レッドストーンダスト", 15uL, ShopItemRarity.UNCOMMON),
    REDSTONE_TORCH(Material.REDSTONE_TORCH, "レッドストーントーチ", 12uL, ShopItemRarity.UNCOMMON),
    REPEATER(Material.REPEATER, "レッドストーンリピーター", 20uL, ShopItemRarity.UNCOMMON),
    COMPARATOR(Material.COMPARATOR, "レッドストーンコンパレーター", 25uL, ShopItemRarity.UNCOMMON),
    LEVER(Material.LEVER, "レバー", 10uL, ShopItemRarity.COMMON),

    // ボタン全種
    OAK_BUTTON(Material.OAK_BUTTON, "オークのボタン", 8uL, ShopItemRarity.COMMON),
    SPRUCE_BUTTON(Material.SPRUCE_BUTTON, "トウヒのボタン", 8uL, ShopItemRarity.COMMON),
    BIRCH_BUTTON(Material.BIRCH_BUTTON, "シラカバのボタン", 8uL, ShopItemRarity.COMMON),
    JUNGLE_BUTTON(Material.JUNGLE_BUTTON, "ジャングルのボタン", 8uL, ShopItemRarity.COMMON),
    ACACIA_BUTTON(Material.ACACIA_BUTTON, "アカシアのボタン", 8uL, ShopItemRarity.COMMON),
    DARK_OAK_BUTTON(Material.DARK_OAK_BUTTON, "ダークオークのボタン", 8uL, ShopItemRarity.COMMON),

    // 感圧板全種
    OAK_PRESSURE_PLATE(Material.OAK_PRESSURE_PLATE, "オークの感圧板", 10uL, ShopItemRarity.COMMON),
    SPRUCE_PRESSURE_PLATE(Material.SPRUCE_PRESSURE_PLATE, "トウヒの感圧板", 10uL, ShopItemRarity.COMMON),
    BIRCH_PRESSURE_PLATE(Material.BIRCH_PRESSURE_PLATE, "シラカバの感圧板", 10uL, ShopItemRarity.COMMON),
    JUNGLE_PRESSURE_PLATE(Material.JUNGLE_PRESSURE_PLATE, "ジャングルの感圧板", 10uL, ShopItemRarity.COMMON),
    ACACIA_PRESSURE_PLATE(Material.ACACIA_PRESSURE_PLATE, "アカシアの感圧板", 10uL, ShopItemRarity.COMMON),
    DARK_OAK_PRESSURE_PLATE(Material.DARK_OAK_PRESSURE_PLATE, "ダークオークの感圧板", 10uL, ShopItemRarity.COMMON),
    LIGHT_WEIGHTED_PRESSURE_PLATE(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, "重量感圧板（軽）", 18uL, ShopItemRarity.UNCOMMON),
    HEAVY_WEIGHTED_PRESSURE_PLATE(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "重量感圧板（重）", 18uL, ShopItemRarity.UNCOMMON),
    ;

    override val category: ShopCategory = ShopCategory.REDSTONE_BLOCKS

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
