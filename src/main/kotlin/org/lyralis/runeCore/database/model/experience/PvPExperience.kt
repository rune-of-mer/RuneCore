package org.lyralis.runeCore.database.model.experience

/**
 * プレイヤー殺害（PvP）時の経験値定義
 *
 * 被害者のレベルに基づいて経験値を計算する．
 */
object PvPExperience : ExperienceSource {
    override val experience: ULong = 0uL
    override val category: ExperienceCategory = ExperienceCategory.PVP

    /**
     * 基本経験値
     */
    private const val BASE_EXPERIENCE: ULong = 50uL

    /**
     * レベルあたりの追加経験値
     */
    private const val EXPERIENCE_PER_LEVEL: ULong = 10uL

    /**
     * 最大獲得経験値
     */
    private const val MAX_EXPERIENCE: ULong = 500uL

    /**
     * 最小獲得経験値
     */
    private const val MIN_EXPERIENCE: ULong = 25uL

    /**
     * 被害者のレベルに基づいて経験値を計算する
     *
     * 計算式: BASE + (victimLevel * EXPERIENCE_PER_LEVEL)
     * 範囲: MIN_EXPERIENCE 〜 MAX_EXPERIENCE
     *
     * @param victimLevel 被害者のレベル
     * @return 獲得できる経験値
     */
    fun calculateExperience(victimLevel: UInt): ULong {
        val calculated = BASE_EXPERIENCE + (victimLevel.toULong() * EXPERIENCE_PER_LEVEL)
        return calculated.coerceIn(MIN_EXPERIENCE, MAX_EXPERIENCE)
    }

    /**
     * キラーと被害者のレベル差に基づいて経験値を計算する
     *
     * レベル差が大きい場合（格下狩り）は経験値を減少させる．
     * レベル差が逆（格上キル）の場合はボーナスを付与する．
     *
     * @param killerLevel キラーのレベル
     * @param victimLevel 被害者のレベル
     * @return 獲得できる経験値
     */
    fun calculateExperienceWithLevelDiff(
        killerLevel: UInt,
        victimLevel: UInt,
    ): ULong {
        val baseExp = calculateExperience(victimLevel)
        val levelDiff = victimLevel.toInt() - killerLevel.toInt()

        val multiplier =
            when {
                levelDiff >= 10 -> 1.5 // 格上キル（10レベル以上上）: 1.5倍
                levelDiff >= 5 -> 1.25 // 格上キル（5レベル以上上）: 1.25倍
                levelDiff <= -10 -> 0.25 // 格下狩り（10レベル以上下）: 0.25倍
                levelDiff <= -5 -> 0.5 // 格下狩り（5レベル以上下）: 0.5倍
                else -> 1.0 // 同格: 1.0倍
            }

        return (baseExp.toDouble() * multiplier).toULong().coerceIn(MIN_EXPERIENCE, MAX_EXPERIENCE)
    }
}
