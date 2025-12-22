package org.lyralis.runeCore.database.model.money

/**
 * プレイヤー殺害（PvP）時の所持金定義
 *
 * 被害者のレベルに基づいて所持金を計算する．
 */
object PvPMoney : MoneySource {
    override val money: ULong = 0uL
    override val category: MoneyCategory = MoneyCategory.PVP

    /**
     * 基本所持金
     */
    private const val BASE_MONEY: ULong = 100uL

    /**
     * レベルあたりの追加所持金
     */
    private const val MONEY_PER_LEVEL: ULong = 20uL

    /**
     * 最大獲得所持金
     */
    private const val MAX_MONEY: ULong = 1000uL

    /**
     * 最小獲得所持金
     */
    private const val MIN_MONEY: ULong = 50uL

    /**
     * 被害者のレベルに基づいて所持金を計算する
     *
     * 計算式: BASE + (victimLevel * MONEY_PER_LEVEL)
     * 範囲: MIN_MONEY 〜 MAX_MONEY
     *
     * @param victimLevel 被害者のレベル
     * @return 獲得できる所持金
     */
    fun calculateMoney(victimLevel: UInt): ULong {
        val calculated = BASE_MONEY + (victimLevel.toULong() * MONEY_PER_LEVEL)
        return calculated.coerceIn(MIN_MONEY, MAX_MONEY)
    }

    /**
     * キラーと被害者のレベル差に基づいて所持金を計算する
     *
     * レベル差が大きい場合（格下狩り）は所持金を減少させる．
     * レベル差が逆（格上キル）の場合はボーナスを付与する．
     *
     * @param killerLevel キラーのレベル
     * @param victimLevel 被害者のレベル
     * @return 獲得できる所持金
     */
    fun calculateMoneyWithLevelDiff(
        killerLevel: UInt,
        victimLevel: UInt,
    ): ULong {
        val baseMoney = calculateMoney(victimLevel)
        val levelDiff = victimLevel.toInt() - killerLevel.toInt()

        val multiplier =
            when {
                levelDiff >= 10 -> 1.5 // 格上キル（10レベル以上上）: 1.5倍
                levelDiff >= 5 -> 1.25 // 格上キル（5レベル以上上）: 1.25倍
                levelDiff <= -10 -> 0.25 // 格下狩り（10レベル以上下）: 0.25倍
                levelDiff <= -5 -> 0.5 // 格下狩り（5レベル以上下）: 0.5倍
                else -> 1.0 // 同格: 1.0倍
            }

        return (baseMoney.toDouble() * multiplier).toULong().coerceIn(MIN_MONEY, MAX_MONEY)
    }
}
