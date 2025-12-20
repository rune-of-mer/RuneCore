package org.lyralis.runeCore.database.impl.experience

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ExperienceBossBarManager {
    private val bossBars = ConcurrentHashMap<UUID, BossBar>()

    private const val THRESHOLD_80 = 79u // 99 * 0.8
    private const val THRESHOLD_60 = 59u // 99 * 0.6
    private const val THRESHOLD_40 = 39u // 99 * 0.4
    private const val THRESHOLD_20 = 19u // 99 * 0.2

    /**
     * ボスバーを現在の経験値に合わせて更新します．
     *
     * @param player 対象プレイヤー
     * @param level 現在のレベル
     * @param totalExperience 現在の総経験値
     */
    fun updateBossBar(
        player: Player,
        level: UInt,
        totalExperience: ULong,
    ) {
        val bossBar =
            bossBars.computeIfAbsent(player.uniqueId) {
                createBossBar()
            }

        val progress = ExperienceCalculator.calculateProgress(totalExperience, level)
        val nextLevelExperience = ExperienceCalculator.getExperienceForLevel(level + 1u)
        val title = buildBossBarTitle(level, totalExperience, nextLevelExperience)
        val color = determineBossBarColor(level)

        bossBar.name(title)
        bossBar.color(color)
        bossBar.progress(progress.coerceIn(0f, 1f))

        if (!player.activeBossBars().contains(bossBar)) {
            player.showBossBar(bossBar)
        }
    }

    /**
     * 指定したプレイヤーからボスバーを削除します．
     *
     * @param player プレイヤー
     */
    fun removeBossBar(player: Player) {
        bossBars.remove(player.uniqueId)?.let { bossBar ->
            player.hideBossBar(bossBar)
        }
    }

    /**
     * 現在存在するすべてのボスバーを削除します．
     */
    fun removeAllBossBars() {
        bossBars.clear()
    }

    /**
     * 指定したプレイヤーにボスバーが表示されているかどうかを確認します．
     *
     * @param player プレイヤー
     * @return 表示している場合 true, 表示していない場合は false が返ってきます．
     */
    fun hasBossBar(player: Player): Boolean = bossBars.containsKey(player.uniqueId)

    private fun createBossBar(): BossBar =
        BossBar.bossBar(
            Component.empty(),
            0f,
            BossBar.Color.GREEN,
            BossBar.Overlay.PROGRESS,
        )

    private fun buildBossBarTitle(
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
                .append(Component.text("$level ").color(NamedTextColor.YELLOW))
                .append(Component.text("[MAX]").color(NamedTextColor.RED))
        } else {
            Component
                .text("レベル ")
                .color(NamedTextColor.GREEN)
                .append(Component.text("$level ").color(NamedTextColor.YELLOW))
                .append(Component.text("| ").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("$currentExperience").color(NamedTextColor.WHITE))
                .append(Component.text(" / ").color(NamedTextColor.GRAY))
                .append(Component.text("$requiredExperience").color(NamedTextColor.WHITE))
                .append(Component.text(" EXP").color(NamedTextColor.AQUA))
        }
    }

    private fun determineBossBarColor(level: UInt): BossBar.Color {
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
