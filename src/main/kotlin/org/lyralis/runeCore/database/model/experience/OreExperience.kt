package org.lyralis.runeCore.experience.source

import org.bukkit.Material
import org.lyralis.runeCore.database.model.experience.ExperienceCategory
import org.lyralis.runeCore.database.model.experience.ExperienceSource

/**
 * 鉱石破壊時の経験値定義
 *
 * 各鉱石ブロックに対応する経験値とカテゴリを定義する．
 * シルクタッチで採掘した場合は経験値を付与しない想定．
 */
enum class OreExperience(
    val material: Material,
    override val experience: ULong,
    override val category: ExperienceCategory,
) : ExperienceSource {
    // ========== 貴重鉱石 ==========
    ANCIENT_DEBRIS(Material.ANCIENT_DEBRIS, 100uL, ExperienceCategory.ORE_PRECIOUS),
    DIAMOND_ORE(Material.DIAMOND_ORE, 50uL, ExperienceCategory.ORE_PRECIOUS),
    DEEPSLATE_DIAMOND_ORE(Material.DEEPSLATE_DIAMOND_ORE, 55uL, ExperienceCategory.ORE_PRECIOUS),
    EMERALD_ORE(Material.EMERALD_ORE, 60uL, ExperienceCategory.ORE_PRECIOUS),
    DEEPSLATE_EMERALD_ORE(Material.DEEPSLATE_EMERALD_ORE, 65uL, ExperienceCategory.ORE_PRECIOUS),

    // ========== 一般鉱石（オーバーワールド） ==========
    COAL_ORE(Material.COAL_ORE, 5uL, ExperienceCategory.ORE_COMMON),
    DEEPSLATE_COAL_ORE(Material.DEEPSLATE_COAL_ORE, 6uL, ExperienceCategory.ORE_COMMON),
    IRON_ORE(Material.IRON_ORE, 10uL, ExperienceCategory.ORE_COMMON),
    DEEPSLATE_IRON_ORE(Material.DEEPSLATE_IRON_ORE, 12uL, ExperienceCategory.ORE_COMMON),
    COPPER_ORE(Material.COPPER_ORE, 8uL, ExperienceCategory.ORE_COMMON),
    DEEPSLATE_COPPER_ORE(Material.DEEPSLATE_COPPER_ORE, 10uL, ExperienceCategory.ORE_COMMON),
    GOLD_ORE(Material.GOLD_ORE, 20uL, ExperienceCategory.ORE_COMMON),
    DEEPSLATE_GOLD_ORE(Material.DEEPSLATE_GOLD_ORE, 22uL, ExperienceCategory.ORE_COMMON),
    LAPIS_ORE(Material.LAPIS_ORE, 25uL, ExperienceCategory.ORE_COMMON),
    DEEPSLATE_LAPIS_ORE(Material.DEEPSLATE_LAPIS_ORE, 28uL, ExperienceCategory.ORE_COMMON),
    REDSTONE_ORE(Material.REDSTONE_ORE, 15uL, ExperienceCategory.ORE_COMMON),
    DEEPSLATE_REDSTONE_ORE(Material.DEEPSLATE_REDSTONE_ORE, 18uL, ExperienceCategory.ORE_COMMON),

    // ========== ネザー鉱石 ==========
    NETHER_GOLD_ORE(Material.NETHER_GOLD_ORE, 15uL, ExperienceCategory.ORE_NETHER),
    NETHER_QUARTZ_ORE(Material.NETHER_QUARTZ_ORE, 12uL, ExperienceCategory.ORE_NETHER),
    ;

    companion object {
        private val byMaterial: Map<Material, OreExperience> =
            entries.associateBy { it.material }

        /**
         * Material から経験値を取得する
         *
         * @param material ブロックのマテリアル
         * @return 経験値．未定義の場合は 0
         */
        fun getExperience(material: Material): ULong = byMaterial[material]?.experience ?: 0uL

        /**
         * Material から OreExperience を取得する
         *
         * @param material ブロックのマテリアル
         * @return OreExperience．未定義の場合は null
         */
        fun fromMaterial(material: Material): OreExperience? = byMaterial[material]

        /**
         * カテゴリでフィルタリングする
         *
         * @param category フィルタするカテゴリ
         * @return 該当する OreExperience のリスト
         */
        fun byCategory(category: ExperienceCategory): List<OreExperience> = entries.filter { it.category == category }

        /**
         * 経験値が定義されているかどうか（鉱石ブロックかどうか）
         *
         * @param material ブロックのマテリアル
         * @return 定義されている場合は true
         */
        fun isOre(material: Material): Boolean = byMaterial.containsKey(material)
    }
}
