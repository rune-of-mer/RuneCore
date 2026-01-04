package org.lyralis.runeCore.domain.shop

import org.bukkit.Material

/**
 * ショップカテゴリー定義
 *
 * @param displayName カテゴリーの表示名
 * @param description カテゴリーの説明
 * @param icon GUIで使用するアイコンマテリアル
 */
enum class ShopCategory(
    val displayName: String,
    val description: String,
    val icon: Material,
) {
    BUILDING_BLOCKS("建築ブロック", "一般的な建築用ブロック", Material.BRICKS),
    COLORED_BLOCKS("色別ブロック", "羊毛・コンクリートなど色付きブロック", Material.WHITE_WOOL),
    DECORATIVE_BLOCKS("装飾ブロック", "装飾用のブロック", Material.FLOWER_POT),
    NATURAL_BLOCKS("自然ブロック", "自然由来のブロック", Material.GRASS_BLOCK),
    REDSTONE_BLOCKS("レッドストーン", "レッドストーン関連アイテム", Material.REDSTONE),
    TOOLS_UTILITIES("ツール・ユーティリティ", "ツールと便利アイテム", Material.DIAMOND_PICKAXE),
    FOOD("食べ物", "食料アイテム", Material.GOLDEN_APPLE),
    MATERIALS("素材", "クラフト素材", Material.IRON_INGOT),
    CUSTOM_ITEMS("カスタムアイテム", "サーバー独自のアイテム", Material.NETHER_STAR),
}
