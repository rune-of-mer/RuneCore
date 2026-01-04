package org.lyralis.runeCore.component.bossbar

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import org.lyralis.runeCore.domain.experience.ExperienceCalculator
import java.util.UUID

/**
 * 経験値を BossBar に表示するプロバイダー．
 *
 * @param experienceProvider プレイヤーの総経験値を取得する関数
 * @param levelProvider プレイヤーのレベルを取得する関数
 */
class ExperienceBossBarProvider(
    private val experienceProvider: (UUID) -> ULong,
    private val levelProvider: (UUID) -> UInt,
) : PersistentBossBarProvider {
    companion object {
        private const val THRESHOLD_80 = 79u
        private const val THRESHOLD_60 = 59u
        private const val THRESHOLD_40 = 39u
        private const val THRESHOLD_20 = 19u
    }

    override fun getTitle(player: Player): Component {
        val level = levelProvider(player.uniqueId)
        val totalExperience = experienceProvider(player.uniqueId)
        val nextLevelExperience = ExperienceCalculator.getExperienceForLevel(level + 1u)

        return buildTitle(level, totalExperience, nextLevelExperience)
    }

    override fun getProgress(player: Player): Float {
        val level = levelProvider(player.uniqueId)
        val totalExperience = experienceProvider(player.uniqueId)

        return ExperienceCalculator.calculateProgress(totalExperience, level)
    }

    override fun getColor(player: Player): BossBar.Color {
        val level = levelProvider(player.uniqueId)
        return determineColor(level)
    }

    private fun buildTitle(
        level: UInt,
        currentExperience: ULong,
        requiredExperience: ULong,
    ): Component {
        val maxLevel = ExperienceCalculator.getMaxLevel()

        return if (level >= maxLevel) {
            Component
                .text("レベル ")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true)
                .append(Component.text("$level ").color(NamedTextColor.GOLD))
                .append(Component.text("-- MAX --").color(NamedTextColor.RED))
        } else {
            Component
                .text("レベル ")
                .color(NamedTextColor.AQUA)
                .append(Component.text("$level ").color(NamedTextColor.AQUA))
                .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("$currentExperience").color(NamedTextColor.WHITE))
                .append(Component.text(" / ").color(NamedTextColor.GRAY))
                .append(Component.text("$requiredExperience").color(NamedTextColor.WHITE))
                .append(Component.text(" EXP").color(NamedTextColor.AQUA))
        }
    }

    private fun determineColor(level: UInt): BossBar.Color {
        val maxLevel = ExperienceCalculator.getMaxLevel()

        return when {
            level >= maxLevel -> BossBar.Color.RED
            level >= THRESHOLD_80 -> BossBar.Color.PURPLE
            level >= THRESHOLD_60 -> BossBar.Color.BLUE
            level >= THRESHOLD_40 -> BossBar.Color.YELLOW
            level >= THRESHOLD_20 -> BossBar.Color.GREEN
            else -> BossBar.Color.WHITE
        }
    }
}
