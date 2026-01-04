package org.lyralis.runeCore.domain.money

import org.bukkit.Material

/**
 * 鉱石破壊時の所持金定義
 *
 * 各鉱石ブロックに対応する所持金とカテゴリを定義する．
 * シルクタッチで採掘した場合は所持金を付与しない想定．
 */
enum class OreMoney(
    val material: Material,
    override val money: ULong,
    override val category: MoneyCategory,
) : MoneySource {
    // ========== 貴重鉱石 ==========
    ANCIENT_DEBRIS(Material.ANCIENT_DEBRIS, 200uL, MoneyCategory.ORE_PRECIOUS),
    DIAMOND_ORE(Material.DIAMOND_ORE, 100uL, MoneyCategory.ORE_PRECIOUS),
    DEEPSLATE_DIAMOND_ORE(Material.DEEPSLATE_DIAMOND_ORE, 110uL, MoneyCategory.ORE_PRECIOUS),
    EMERALD_ORE(Material.EMERALD_ORE, 120uL, MoneyCategory.ORE_PRECIOUS),
    DEEPSLATE_EMERALD_ORE(Material.DEEPSLATE_EMERALD_ORE, 130uL, MoneyCategory.ORE_PRECIOUS),

    // ========== 一般鉱石（オーバーワールド） ==========
    COAL_ORE(Material.COAL_ORE, 10uL, MoneyCategory.ORE_COMMON),
    DEEPSLATE_COAL_ORE(Material.DEEPSLATE_COAL_ORE, 12uL, MoneyCategory.ORE_COMMON),
    IRON_ORE(Material.IRON_ORE, 20uL, MoneyCategory.ORE_COMMON),
    DEEPSLATE_IRON_ORE(Material.DEEPSLATE_IRON_ORE, 24uL, MoneyCategory.ORE_COMMON),
    COPPER_ORE(Material.COPPER_ORE, 16uL, MoneyCategory.ORE_COMMON),
    DEEPSLATE_COPPER_ORE(Material.DEEPSLATE_COPPER_ORE, 20uL, MoneyCategory.ORE_COMMON),
    GOLD_ORE(Material.GOLD_ORE, 40uL, MoneyCategory.ORE_COMMON),
    DEEPSLATE_GOLD_ORE(Material.DEEPSLATE_GOLD_ORE, 44uL, MoneyCategory.ORE_COMMON),
    LAPIS_ORE(Material.LAPIS_ORE, 50uL, MoneyCategory.ORE_COMMON),
    DEEPSLATE_LAPIS_ORE(Material.DEEPSLATE_LAPIS_ORE, 56uL, MoneyCategory.ORE_COMMON),
    REDSTONE_ORE(Material.REDSTONE_ORE, 30uL, MoneyCategory.ORE_COMMON),
    DEEPSLATE_REDSTONE_ORE(Material.DEEPSLATE_REDSTONE_ORE, 36uL, MoneyCategory.ORE_COMMON),

    // ========== ネザー鉱石 ==========
    NETHER_GOLD_ORE(Material.NETHER_GOLD_ORE, 30uL, MoneyCategory.ORE_NETHER),
    NETHER_QUARTZ_ORE(Material.NETHER_QUARTZ_ORE, 24uL, MoneyCategory.ORE_NETHER),
    ;

    companion object {
        private val byMaterial: Map<Material, OreMoney> =
            entries.associateBy { it.material }

        /**
         * Material から所持金を取得する
         *
         * @param material ブロックのマテリアル
         * @return 所持金．未定義の場合は 0
         */
        fun getMoney(material: Material): ULong = byMaterial[material]?.money ?: 0uL

        /**
         * Material から OreMoney を取得する
         *
         * @param material ブロックのマテリアル
         * @return OreMoney．未定義の場合は null
         */
        fun fromMaterial(material: Material): OreMoney? = byMaterial[material]

        /**
         * カテゴリでフィルタリングする
         *
         * @param category フィルタするカテゴリ
         * @return 該当する OreMoney のリスト
         */
        fun byCategory(category: MoneyCategory): List<OreMoney> = entries.filter { it.category == category }

        /**
         * 所持金が定義されているかどうか（鉱石ブロックかどうか）
         *
         * @param material ブロックのマテリアル
         * @return 定義されている場合は true
         */
        fun isOre(material: Material): Boolean = byMaterial.containsKey(material)
    }
}
