package org.lyralis.runeCore.utils

import org.bukkit.Material

/**
 * ブロック破壊時に統計集計から除外するマテリアルリスト
 */
object ExcludedBlockMaterials {
    private val EXCLUDED_MATERIALS =
        setOf(
            // ========== 花 ==========
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.LILY_OF_THE_VALLEY,
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,
            Material.DANDELION,
            // ========== 草・植物系 ==========
            Material.GRASS_BLOCK,
            Material.TALL_GRASS,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.DEAD_BUSH,
            Material.VINE,
            Material.GLOW_LICHEN,
            Material.LILY_PAD,
            Material.BIG_DRIPLEAF,
            Material.BIG_DRIPLEAF_STEM,
            Material.SMALL_DRIPLEAF,
            Material.KELP,
            Material.KELP_PLANT,
            Material.HANGING_ROOTS,
            // ========== キノコ ==========
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.CRIMSON_FUNGUS,
            Material.WARPED_FUNGUS,
            // ========== 光源・装飾 ==========
            Material.TORCH,
            Material.WALL_TORCH,
            Material.SOUL_TORCH,
            Material.SOUL_WALL_TORCH,
            Material.REDSTONE_TORCH,
            Material.REDSTONE_WALL_TORCH,
            Material.LANTERN,
            Material.SOUL_LANTERN,
            Material.CANDLE,
            Material.WHITE_CANDLE,
            Material.ORANGE_CANDLE,
            Material.MAGENTA_CANDLE,
            Material.LIGHT_BLUE_CANDLE,
            Material.YELLOW_CANDLE,
            Material.LIME_CANDLE,
            Material.PINK_CANDLE,
            Material.GRAY_CANDLE,
            Material.LIGHT_GRAY_CANDLE,
            Material.CYAN_CANDLE,
            Material.PURPLE_CANDLE,
            Material.BLUE_CANDLE,
            Material.BROWN_CANDLE,
            Material.GREEN_CANDLE,
            Material.RED_CANDLE,
            Material.BLACK_CANDLE,
            Material.CANDLE_CAKE,
            Material.WHITE_CANDLE_CAKE,
            Material.ORANGE_CANDLE_CAKE,
            Material.MAGENTA_CANDLE_CAKE,
            Material.LIGHT_BLUE_CANDLE_CAKE,
            Material.YELLOW_CANDLE_CAKE,
            Material.LIME_CANDLE_CAKE,
            Material.PINK_CANDLE_CAKE,
            Material.GRAY_CANDLE_CAKE,
            Material.LIGHT_GRAY_CANDLE_CAKE,
            Material.CYAN_CANDLE_CAKE,
            Material.PURPLE_CANDLE_CAKE,
            Material.BLUE_CANDLE_CAKE,
            Material.BROWN_CANDLE_CAKE,
            Material.GREEN_CANDLE_CAKE,
            Material.RED_CANDLE_CAKE,
            Material.BLACK_CANDLE_CAKE,
            Material.JACK_O_LANTERN,
            Material.SEA_LANTERN,
            Material.GLOWSTONE,
            Material.SHROOMLIGHT,
            Material.AMETHYST_CLUSTER,
            Material.SMALL_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD,
            Material.LARGE_AMETHYST_BUD,
            // ========== つる・蔦系 ==========
            Material.TWISTING_VINES,
            Material.TWISTING_VINES_PLANT,
            Material.WEEPING_VINES,
            Material.WEEPING_VINES_PLANT,
            // ========== その他装飾 ==========
            Material.FLOWER_POT,
            Material.POTTED_POPPY,
            Material.POTTED_BLUE_ORCHID,
            Material.POTTED_ALLIUM,
            Material.POTTED_AZURE_BLUET,
            Material.POTTED_RED_TULIP,
            Material.POTTED_ORANGE_TULIP,
            Material.POTTED_WHITE_TULIP,
            Material.POTTED_PINK_TULIP,
            Material.POTTED_OXEYE_DAISY,
            Material.POTTED_DANDELION,
            Material.POTTED_BROWN_MUSHROOM,
            Material.POTTED_RED_MUSHROOM,
            Material.POTTED_DEAD_BUSH,
            Material.POTTED_FERN,
            Material.POTTED_JUNGLE_SAPLING,
            Material.POTTED_OAK_SAPLING,
            Material.POTTED_SPRUCE_SAPLING,
            Material.POTTED_BIRCH_SAPLING,
            Material.POTTED_ACACIA_SAPLING,
            Material.POTTED_DARK_OAK_SAPLING,
            Material.POTTED_WARPED_FUNGUS,
            Material.POTTED_CRIMSON_FUNGUS,
            Material.POTTED_WARPED_ROOTS,
            Material.POTTED_CRIMSON_ROOTS,
            Material.POTTED_AZURE_BLUET,
            Material.POTTED_BLUE_ORCHID,
            Material.POTTED_CORNFLOWER,
            Material.POTTED_LILY_OF_THE_VALLEY,
            // ========== サボテン・スイカなど ==========
            Material.CACTUS,
            Material.SUGAR_CANE,
            Material.MELON,
            Material.PUMPKIN,
            Material.CARVED_PUMPKIN,
            // ========== つる植物 ==========
            Material.COCOA,
            Material.CAVE_VINES,
            Material.CAVE_VINES_PLANT,
        )

    /**
     * 指定したマテリアルが除外対象かどうかを判定する
     *
     * @param material 判定するマテリアル
     * @return 除外対象の場合は true
     */
    fun isExcluded(material: Material): Boolean = material in EXCLUDED_MATERIALS

    /**
     * 除外マテリアルのリストを取得する
     *
     * @return 除外対象のマテリアルセット
     */
    fun getExcludedMaterials(): Set<Material> = EXCLUDED_MATERIALS
}
