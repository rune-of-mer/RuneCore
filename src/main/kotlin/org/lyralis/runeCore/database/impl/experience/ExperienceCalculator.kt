package org.lyralis.runeCore.database.impl.experience

import kotlin.math.floor
import kotlin.math.pow

object ExperienceCalculator {
    /**
     * 現在の RuneCore における最大レベル
     */
    private const val MAX_LEVEL = 99u

    /**
     * 現在の RuneCore におけるベースの経験値
     */
    private const val BASE_EXPERIENCE = 100.0

    /**
     * 現在の RuneCore における経験値の乗数
     */
    private const val EXPERIENCE_MULTIPLIER = 1.5

    /**
     * レベルの計算を行います．
     *
     * @param totalExperience 総経験値数
     * @return 現在のレベル
     */
    fun calculateLevel(totalExperience: ULong): UInt {
        if (totalExperience == 0uL) return 1u

        var level = 1u
        var requiredExperience = getExperienceForLevel(level + 1u)

        while (totalExperience >= requiredExperience && level < MAX_LEVEL) {
            level++
            requiredExperience = getExperienceForLevel(level + 1u)
        }

        return level
    }

    /**
     * 特定のレベルに到達するために必要な経験値を計算する
     *
     * @param targetLevel 計算したい特定のレベル
     * @return 必要な経験値
     */
    fun getExperienceForLevel(targetLevel: UInt): ULong {
        if (targetLevel <= 1u) {
            return 0uL
        }

        var totalExperience = 0uL
        for (level in 2..targetLevel.toInt()) {
            totalExperience += getExperienceForNextLevel((level - 1).toUInt())
        }

        return totalExperience
    }

    /**
     * 現在のレベルから次のレベルへ進むために必要な経験値を計算する
     *
     * @param currentLevel 現在のレベル
     * @param Long 必要な経験値
     */
    fun getExperienceForNextLevel(currentLevel: UInt): ULong {
        if (currentLevel == MAX_LEVEL) {
            return ULong.MAX_VALUE
        }

        return floor(BASE_EXPERIENCE * currentLevel.toDouble().pow(EXPERIENCE_MULTIPLIER)).toULong()
    }

    /**
     * 現在のレベルにおける経験値の進捗率を計算する．
     *
     * @param totalExperience 現在の総経験値
     * @param currentLevel 現在のレベル
     * @return 進捗率 (0.0 から 1.0)
     */
    fun calculateProgress(
        totalExperience: ULong,
        currentLevel: UInt,
    ): Double {
        if (currentLevel >= MAX_LEVEL) {
            return 1.0
        }

        val currentExperience = getExperienceForLevel(currentLevel)
        val nextLevelExperience = getExperienceForLevel(currentLevel + 1u)
        val requiredExperience = nextLevelExperience - currentExperience
        val currentProgress = totalExperience - currentExperience

        return (currentProgress.toDouble() / requiredExperience.toDouble()).coerceIn(0.0, 1.0)
    }

    /**
     * 現在の最大レベルを返す．
     *
     * @return [MAX_LEVEL]
     */
    fun getMaxLevel(): UInt = MAX_LEVEL

    /**
     * 現在の経験値の乗数を返す．
     *
     * @return [EXPERIENCE_MULTIPLIER]
     */
    fun getMultiplier(): Double = EXPERIENCE_MULTIPLIER
}
