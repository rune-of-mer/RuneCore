package org.lyralis.runeCore.domain.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.domain.shop.ShopCategory
import org.lyralis.runeCore.domain.shop.ShopItem
import org.lyralis.runeCore.domain.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * 建築ブロックカテゴリーのアイテム定義
 */
enum class BuildingBlocksShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = true,
    override val customItemId: String? = null,
) : ShopItem {
    // 原木
    OAK_LOG(Material.OAK_LOG, "オークの原木", 8uL, ShopItemRarity.COMMON),
    SPRUCE_LOG(Material.SPRUCE_LOG, "トウヒの原木", 8uL, ShopItemRarity.COMMON),
    BIRCH_LOG(Material.BIRCH_LOG, "シラカバの原木", 8uL, ShopItemRarity.COMMON),
    JUNGLE_LOG(Material.JUNGLE_LOG, "ジャングルの原木", 8uL, ShopItemRarity.COMMON),
    ACACIA_LOG(Material.ACACIA_LOG, "アカシアの原木", 8uL, ShopItemRarity.COMMON),
    DARK_OAK_LOG(Material.DARK_OAK_LOG, "ダークオークの原木", 8uL, ShopItemRarity.COMMON),

    // 樹皮を剥いだ原木
    STRIPPED_OAK_LOG(Material.STRIPPED_OAK_LOG, "樹皮を剥いだオークの原木", 10uL, ShopItemRarity.COMMON),
    STRIPPED_SPRUCE_LOG(Material.STRIPPED_SPRUCE_LOG, "樹皮を剥いだトウヒの原木", 10uL, ShopItemRarity.COMMON),
    STRIPPED_BIRCH_LOG(Material.STRIPPED_BIRCH_LOG, "樹皮を剥いだシラカバの原木", 10uL, ShopItemRarity.COMMON),
    STRIPPED_JUNGLE_LOG(Material.STRIPPED_JUNGLE_LOG, "樹皮を剥いだジャングルの原木", 10uL, ShopItemRarity.COMMON),
    STRIPPED_ACACIA_LOG(Material.STRIPPED_ACACIA_LOG, "樹皮を剥いだアカシアの原木", 10uL, ShopItemRarity.COMMON),
    STRIPPED_DARK_OAK_LOG(Material.STRIPPED_DARK_OAK_LOG, "樹皮を剥いだダークオークの原木", 10uL, ShopItemRarity.COMMON),

    // 板材
    OAK_PLANKS(Material.OAK_PLANKS, "オークの板材", 6uL, ShopItemRarity.COMMON),
    SPRUCE_PLANKS(Material.SPRUCE_PLANKS, "トウヒの板材", 6uL, ShopItemRarity.COMMON),
    BIRCH_PLANKS(Material.BIRCH_PLANKS, "シラカバの板材", 6uL, ShopItemRarity.COMMON),
    JUNGLE_PLANKS(Material.JUNGLE_PLANKS, "ジャングルの板材", 6uL, ShopItemRarity.COMMON),
    ACACIA_PLANKS(Material.ACACIA_PLANKS, "アカシアの板材", 6uL, ShopItemRarity.COMMON),
    DARK_OAK_PLANKS(Material.DARK_OAK_PLANKS, "ダークオークの板材", 6uL, ShopItemRarity.COMMON),

    // 石系
    STONE(Material.STONE, "石", 5uL, ShopItemRarity.COMMON),
    COBBLESTONE(Material.COBBLESTONE, "丸石", 5uL, ShopItemRarity.COMMON),
    MOSSY_COBBLESTONE(Material.MOSSY_COBBLESTONE, "苔むした丸石", 7uL, ShopItemRarity.COMMON),
    SMOOTH_STONE(Material.SMOOTH_STONE, "滑らかな石", 7uL, ShopItemRarity.COMMON),
    STONE_BRICKS(Material.STONE_BRICKS, "石レンガ", 8uL, ShopItemRarity.COMMON),
    MOSSY_STONE_BRICKS(Material.MOSSY_STONE_BRICKS, "苔むした石レンガ", 10uL, ShopItemRarity.COMMON),
    CRACKED_STONE_BRICKS(Material.CRACKED_STONE_BRICKS, "ひび割れた石レンガ", 10uL, ShopItemRarity.COMMON),
    CHISELED_STONE_BRICKS(Material.CHISELED_STONE_BRICKS, "模様入りの石レンガ", 12uL, ShopItemRarity.UNCOMMON),

    // 花崗岩系
    GRANITE(Material.GRANITE, "花崗岩", 15uL, ShopItemRarity.UNCOMMON),

    // 閃緑岩系
    DIORITE(Material.DIORITE, "閃緑岩", 15uL, ShopItemRarity.UNCOMMON),

    // 安山岩系
    ANDESITE(Material.ANDESITE, "安山岩", 15uL, ShopItemRarity.UNCOMMON),

    // レンガ系
    BRICKS(Material.BRICKS, "レンガ", 12uL, ShopItemRarity.UNCOMMON),

    // 砂岩系
    SANDSTONE(Material.SANDSTONE, "砂岩", 8uL, ShopItemRarity.COMMON),

    // ネザーラック
    NETHERRACK(Material.NETHERRACK, "ネザーラック", 1uL, ShopItemRarity.UNCOMMON),

    // 銅系
    COPPER_BLOCK(Material.COPPER_BLOCK, "銅ブロック", 30uL, ShopItemRarity.UNCOMMON),
    CUT_COPPER(Material.CUT_COPPER, "切り込み入りの銅", 32uL, ShopItemRarity.UNCOMMON),

    ;

    override val category: ShopCategory = ShopCategory.BUILDING_BLOCKS

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
