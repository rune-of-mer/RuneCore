package org.lyralis.runeCore.domain.gacha

import org.bukkit.Material
import org.lyralis.runeCore.item.ItemRarity

/**
 * ガチャで排出されるバニラアイテムの定義
 *
 * 全てのガチャで共通して排出されるバニラアイテムをレアリティごとに定義する
 *
 * @param material アイテムのマテリアル
 * @param amount 排出される個数
 * @param rarity レアリティ（重み計算に使用）
 */
data class GachaVanillaItem(
    val material: Material,
    val amount: Int,
    val rarity: ItemRarity,
)

/**
 * ガチャで排出されるバニラアイテムのレジストリ
 */
object GachaVanillaItems {
    /**
     * コモンレアリティのバニラアイテム（よく出る）
     */
    val commonItems: List<GachaVanillaItem> =
        listOf(
            // 鉱石・素材
            GachaVanillaItem(Material.IRON_INGOT, 2, ItemRarity.COMMON),
            GachaVanillaItem(Material.GOLD_INGOT, 2, ItemRarity.COMMON),
            GachaVanillaItem(Material.COAL, 16, ItemRarity.COMMON),
            // 食料
            GachaVanillaItem(Material.BREAD, 16, ItemRarity.COMMON),
            GachaVanillaItem(Material.PUMPKIN_PIE, 16, ItemRarity.COMMON),
            // 建材
            GachaVanillaItem(Material.OAK_LOG, 32, ItemRarity.COMMON),
            GachaVanillaItem(Material.COBBLESTONE, 64, ItemRarity.COMMON),
            // その他
            GachaVanillaItem(Material.ARROW, 5, ItemRarity.COMMON),
            GachaVanillaItem(Material.BONE, 16, ItemRarity.COMMON),
            GachaVanillaItem(Material.STRING, 16, ItemRarity.COMMON),
            GachaVanillaItem(Material.GUNPOWDER, 8, ItemRarity.COMMON),
        )

    /**
     * レアレアリティのバニラアイテム（稀に出る）
     */
    val rareItems: List<GachaVanillaItem> =
        listOf(
            // 鉱石・素材
            GachaVanillaItem(Material.DIAMOND, 4, ItemRarity.RARE),
            GachaVanillaItem(Material.EMERALD, 4, ItemRarity.RARE),
            GachaVanillaItem(Material.NETHERITE_SCRAP, 2, ItemRarity.RARE),
            GachaVanillaItem(Material.ANCIENT_DEBRIS, 1, ItemRarity.RARE),
            // 装備品
            GachaVanillaItem(Material.DIAMOND_SWORD, 1, ItemRarity.RARE),
            // レアアイテム
            GachaVanillaItem(Material.ENCHANTED_GOLDEN_APPLE, 1, ItemRarity.RARE),
            // エンチャント本
            GachaVanillaItem(Material.EXPERIENCE_BOTTLE, 32, ItemRarity.RARE),
        )

    /**
     * 全バニラアイテムを取得
     */
    fun all(): List<GachaVanillaItem> = commonItems + rareItems

    /**
     * レアリティでフィルタリング
     */
    fun byRarity(rarity: ItemRarity): List<GachaVanillaItem> = all().filter { it.rarity == rarity }
}
