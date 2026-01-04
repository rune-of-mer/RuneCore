package org.lyralis.runeCore.domain.shop.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.domain.shop.ShopCategory
import org.lyralis.runeCore.domain.shop.ShopItem
import org.lyralis.runeCore.domain.shop.ShopItemRarity
import org.lyralis.runeCore.item.ItemRegistry

/**
 * ツール・ユーティリティカテゴリーのアイテム定義
 */
enum class ToolsUtilitiesShopItem(
    override val material: Material,
    override val displayName: String,
    override val price: ULong,
    override val rarity: ShopItemRarity,
    override val description: List<String> = emptyList(),
    override val bulkPurchaseEnabled: Boolean = false,
    override val customItemId: String? = null,
) : ShopItem {
    // 石の道具
    STONE_PICKAXE(Material.STONE_PICKAXE, "石のツルハシ", 30uL, ShopItemRarity.COMMON),
    STONE_AXE(Material.STONE_AXE, "石の斧", 30uL, ShopItemRarity.COMMON),
    STONE_SHOVEL(Material.STONE_SHOVEL, "石のシャベル", 25uL, ShopItemRarity.COMMON),
    STONE_HOE(Material.STONE_HOE, "石のクワ", 25uL, ShopItemRarity.COMMON),

    // 木の道具
    WOODEN_PICKAXE(Material.WOODEN_PICKAXE, "木のツルハシ", 20uL, ShopItemRarity.COMMON),
    WOODEN_AXE(Material.WOODEN_AXE, "木の斧", 20uL, ShopItemRarity.COMMON),
    WOODEN_SHOVEL(Material.WOODEN_SHOVEL, "木のシャベル", 15uL, ShopItemRarity.COMMON),
    WOODEN_HOE(Material.WOODEN_HOE, "木のクワ", 15uL, ShopItemRarity.COMMON),

    // その他
    BUCKET(Material.BUCKET, "バケツ", 40uL, ShopItemRarity.UNCOMMON),
    FISHING_ROD(Material.FISHING_ROD, "釣竿", 35uL, ShopItemRarity.UNCOMMON),
    FLINT_AND_STEEL(Material.FLINT_AND_STEEL, "火打ち石", 30uL, ShopItemRarity.UNCOMMON),
    WRITABLE_BOOK(Material.WRITABLE_BOOK, "本と羽ペン", 50uL, ShopItemRarity.UNCOMMON),
    SADDLE(Material.SADDLE, "鞍", 100uL, ShopItemRarity.RARE),
    CLOCK(Material.CLOCK, "時計", 80uL, ShopItemRarity.UNCOMMON),
    COMPASS(Material.COMPASS, "コンパス", 80uL, ShopItemRarity.UNCOMMON),
    BUNDLE(Material.BUNDLE, "バンドル", 120uL, ShopItemRarity.RARE),
    LEAD(Material.LEAD, "リード", 45uL, ShopItemRarity.UNCOMMON),
    NAME_TAG(Material.NAME_TAG, "名札", 200uL, ShopItemRarity.EPIC),
    SHEARS(Material.SHEARS, "ハサミ", 35uL, ShopItemRarity.UNCOMMON),

    // 石の武器
    STONE_SWORD(Material.STONE_SWORD, "石の剣", 40uL, ShopItemRarity.COMMON),

    // 木の武器
    WOODEN_SWORD(Material.WOODEN_SWORD, "木の剣", 25uL, ShopItemRarity.COMMON),

    // 皮の防具
    LEATHER_HELMET(Material.LEATHER_HELMET, "革の帽子", 50uL, ShopItemRarity.COMMON),
    LEATHER_CHESTPLATE(Material.LEATHER_CHESTPLATE, "革の上着", 80uL, ShopItemRarity.COMMON),
    LEATHER_LEGGINGS(Material.LEATHER_LEGGINGS, "革のズボン", 70uL, ShopItemRarity.COMMON),
    LEATHER_BOOTS(Material.LEATHER_BOOTS, "革のブーツ", 40uL, ShopItemRarity.COMMON),

    // 遠距離武器
    BOW(Material.BOW, "弓", 100uL, ShopItemRarity.UNCOMMON),
    CROSSBOW(Material.CROSSBOW, "クロスボウ", 150uL, ShopItemRarity.UNCOMMON),

    ;

    override val category: ShopCategory = ShopCategory.TOOLS_UTILITIES

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
