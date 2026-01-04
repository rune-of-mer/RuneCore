package org.lyralis.runeCore.domain.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.domain.shop.ShopCategory
import org.lyralis.runeCore.domain.shop.ShopItem
import org.lyralis.runeCore.domain.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * 自然ブロックカテゴリーのアイテム定義
 */
enum class NaturalBlocksShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    // 自然ブロック
    GRASS_BLOCK(Material.GRASS_BLOCK, "草ブロック", 8uL, ShopItemRarity.COMMON),
    DIRT(Material.DIRT, "土", 5uL, ShopItemRarity.COMMON),
    SAND(Material.SAND, "砂", 6uL, ShopItemRarity.COMMON),
    ICE(Material.ICE, "氷", 10uL, ShopItemRarity.COMMON),

    // 苗木
    OAK_SAPLING(Material.OAK_SAPLING, "オークの苗木", 25uL, ShopItemRarity.UNCOMMON),
    SPRUCE_SAPLING(Material.SPRUCE_SAPLING, "トウヒの苗木", 25uL, ShopItemRarity.UNCOMMON),
    BIRCH_SAPLING(Material.BIRCH_SAPLING, "シラカバの苗木", 25uL, ShopItemRarity.UNCOMMON),
    JUNGLE_SAPLING(Material.JUNGLE_SAPLING, "ジャングルの苗木", 25uL, ShopItemRarity.UNCOMMON),
    ACACIA_SAPLING(Material.ACACIA_SAPLING, "アカシアの苗木", 25uL, ShopItemRarity.UNCOMMON),
    DARK_OAK_SAPLING(Material.DARK_OAK_SAPLING, "ダークオークの苗木", 25uL, ShopItemRarity.UNCOMMON),
    CHERRY_SAPLING(Material.CHERRY_SAPLING, "サクラの苗木", 30uL, ShopItemRarity.UNCOMMON),
    MANGROVE_PROPAGULE(Material.MANGROVE_PROPAGULE, "マングローブの芽", 25uL, ShopItemRarity.UNCOMMON),

    // キノコ
    RED_MUSHROOM(Material.RED_MUSHROOM, "赤色のキノコ", 12uL, ShopItemRarity.COMMON),
    BROWN_MUSHROOM(Material.BROWN_MUSHROOM, "茶色のキノコ", 12uL, ShopItemRarity.COMMON),

    // 植物
    TALL_GRASS(Material.TALL_GRASS, "背の高い草", 8uL, ShopItemRarity.COMMON),

    // 種
    WHEAT_SEEDS(Material.WHEAT_SEEDS, "小麦の種", 10uL, ShopItemRarity.COMMON),

    // 作物
    PUMPKIN(Material.PUMPKIN, "カボチャ", 15uL, ShopItemRarity.COMMON),
    ;

    override val category: ShopCategory = ShopCategory.NATURAL_BLOCKS

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
