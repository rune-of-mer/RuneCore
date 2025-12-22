package org.lyralis.runeCore.database.model.money

/**
 * 所持金ソースの根底インターフェース
 *
 * 所持金が獲得できる全てのソースを実装する
 */
interface MoneySource {
    /**
     * プレイヤーが獲得できる所持金
     */
    val money: ULong

    /**
     * ソースのカテゴリー
     */
    val category: MoneyCategory
}

/**
 * 所持金ソースのカテゴリー
 *
 * @param displayName カテゴリー名
 * @param description カテゴリー説明
 */
enum class MoneyCategory(
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
