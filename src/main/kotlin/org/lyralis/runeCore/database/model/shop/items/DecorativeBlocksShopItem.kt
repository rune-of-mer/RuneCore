package org.lyralis.runeCore.database.model.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.model.shop.ShopCategory
import org.lyralis.runeCore.database.model.shop.ShopItem
import org.lyralis.runeCore.database.model.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * 装飾ブロックカテゴリーのアイテム定義
 */
enum class DecorativeBlocksShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    TORCH(Material.TORCH, "松明", 5uL, ShopItemRarity.COMMON),
    LANTERN(Material.LANTERN, "ランタン", 15uL, ShopItemRarity.UNCOMMON),
    REDSTONE_LAMP(Material.REDSTONE_LAMP, "レッドストーンランプ", 20uL, ShopItemRarity.UNCOMMON),
    LADDER(Material.LADDER, "はしご", 8uL, ShopItemRarity.COMMON),
    SCAFFOLDING(Material.SCAFFOLDING, "足場", 12uL, ShopItemRarity.COMMON),
    ITEM_FRAME(Material.ITEM_FRAME, "額縁", 15uL, ShopItemRarity.UNCOMMON),

    // 看板全種
    OAK_SIGN(Material.OAK_SIGN, "オークの看板", 10uL, ShopItemRarity.COMMON),
    SPRUCE_SIGN(Material.SPRUCE_SIGN, "トウヒの看板", 10uL, ShopItemRarity.COMMON),
    BIRCH_SIGN(Material.BIRCH_SIGN, "シラカバの看板", 10uL, ShopItemRarity.COMMON),
    JUNGLE_SIGN(Material.JUNGLE_SIGN, "ジャングルの看板", 10uL, ShopItemRarity.COMMON),
    ACACIA_SIGN(Material.ACACIA_SIGN, "アカシアの看板", 10uL, ShopItemRarity.COMMON),
    DARK_OAK_SIGN(Material.DARK_OAK_SIGN, "ダークオークの看板", 10uL, ShopItemRarity.COMMON),

    CHEST(Material.CHEST, "チェスト", 20uL, ShopItemRarity.UNCOMMON),
    BARREL(Material.BARREL, "樽", 18uL, ShopItemRarity.UNCOMMON),
    BOOKSHELF(Material.BOOKSHELF, "本棚", 25uL, ShopItemRarity.UNCOMMON),
    CAMPFIRE(Material.CAMPFIRE, "焚き火", 15uL, ShopItemRarity.UNCOMMON),
    ;

    override val category: ShopCategory = ShopCategory.DECORATIVE_BLOCKS

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
