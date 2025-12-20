package org.lyralis.runeCore.database.model.experience

/**
 * 経験値ソースの根底インターフェース
 *
 * 経験値が獲得できる全てのソースを実装する
 */
interface ExperienceSource {
    /**
     * プレイヤーが獲得できる経験値
     */
    val experience: ULong

    /**
     * ソースのカテゴリー
     */
    val category: ExperienceCategory
}

/**
 * 経験値ソースのカテゴリー
 *
 * @param displayName カテゴリー名
 * @param description カテゴリー説明
 */
enum class ExperienceCategory(
    val displayName: String,
    val description: String,
) {
    // モブ関連
    MOB_BOSS("ボス", "ボスモブの討伐"),
    MOB_HOSTILE("敵対モブ", "敵対モブの討伐"),
    MOB_NATURAL("中立モブ", "中立モブの討伐"),
    MOB_PASSIVE("友好モブ", "友好モブの討伐"),

    // 鉱石関連
    ORE_PRECIOUS("貴重鉱石", "貴重な鉱石の採掘"),
    ORE_COMMON("一般鉱石", "一般的な鉱石の採掘"),
    ORE_NETHER("ネザー鉱石", "ネザーの鉱石の採掘"),

    // PvP 関連
    PVP("PvP", "プレイヤーの殺害"),
}
