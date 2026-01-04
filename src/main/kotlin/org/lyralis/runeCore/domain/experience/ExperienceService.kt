package org.lyralis.runeCore.domain.experience

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.bossbar.BossBarManager
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.domain.player.PlayerService
import java.time.Duration
import java.util.UUID
import java.util.logging.Logger

class ExperienceService(
    private val playerService: PlayerService,
    private val logger: Logger,
) {
    /**
     * 指定したプレイヤーに経験値を付与します．
     *
     * @param player プレイヤー
     * @param amount 付与する経験値
     * @return 付与した経験値量を返します．付与に失敗した場合は null が帰ってきます
     */
    fun grantExperience(
        player: Player,
        amount: ULong,
    ): ULong? {
        if (amount == 0uL) {
            return null
        }

        if (player.gameMode == GameMode.CREATIVE) {
            ActionBarManager.showTemporaryNotification(
                player,
                "クリエイティブモードの場合は経験値は獲得できません".errorMessage(),
            )
            return null
        }

        val uuid = player.uniqueId
        val currentExperience = playerService.getExperience(uuid)
        val newExperience = currentExperience + amount
        val oldLevel = playerService.getLevel(uuid)
        val newLevel = ExperienceCalculator.calculateLevel(newExperience)

        if (!playerService.addExperience(uuid, amount)) {
            logger.warning("Failed to grant experience to ${player.name}")
            return null
        }

        if (newLevel != oldLevel) {
            playerService.setLevel(uuid, newLevel)
        }

        BossBarManager.update(player)
        notifyGetExperience(player, amount, newLevel, oldLevel)

        return newExperience
    }

    /**
     * 現在の UUID に保存されているレベルを返します
     *
     * @param uuid 対象の UUID
     * @return 保存されているレベル
     */
    fun getLevel(uuid: UUID): UInt = playerService.getLevel(uuid)

    /**
     * 現在の総経験値を取得する
     *
     * @param uuid UUID
     * @return 現在の総経験値
     */
    fun getExperience(uuid: UUID): ULong = playerService.getExperience(uuid)

    /**
     * 現在の総経験値を読み込みます
     *
     * @param uuid UUID
     */
    fun loadExperience(uuid: UUID) = playerService.loadPlayerData(uuid)

    /**
     * キャッシュを初期化します
     *
     * @param uuid UUID
     */
    fun clearCache(uuid: UUID) = playerService.clearCache(uuid)

    /**
     * 全てのキャッシュを初期化します
     */
    fun clearAllCache() = playerService.clearAllCache()

    private fun notifyGetExperience(
        player: Player,
        addedExperience: ULong,
        newLevel: UInt,
        oldLevel: UInt,
    ) {
        if (newLevel > oldLevel) {
            val title =
                Title.title(
                    Component.text("LEVEL UP!").color(NamedTextColor.YELLOW),
                    Component.text("Lv$oldLevel → Lv$newLevel").color(NamedTextColor.YELLOW),
                    Title.Times.times(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(200),
                    ),
                )
            val sound =
                Sound.sound(
                    org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    Sound.Source.PLAYER,
                    1.0f,
                    1.0f,
                )
            player.apply {
                sendMessage("レベル${newLevel}にあがった".systemMessage())
                server.broadcast("${player.name}はレベル${newLevel}にあがった".systemMessage())
                showTitle(title)
                playSound(sound)
            }
            return
        }

        player.apply {
            val sound =
                Sound.sound(
                    org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_STEP,
                    Sound.Source.PLAYER,
                    0.5f,
                    1.0f,
                )
            ActionBarManager.showTemporaryNotification(this, "+$addedExperience EXP".infoMessage())
            playSound(sound)
        }
    }
}
