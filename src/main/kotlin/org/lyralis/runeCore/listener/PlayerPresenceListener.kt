package org.lyralis.runeCore.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.actionbar.StatusActionBarProvider
import org.lyralis.runeCore.component.bossbar.BossBarManager
import org.lyralis.runeCore.component.bossbar.ExperienceBossBarProvider
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.impl.settings.SettingsService
import org.lyralis.runeCore.gui.cache.PlayerHeadCacheManager

class PlayerPresenceListener(
    private val experienceService: ExperienceService,
    private val moneyService: MoneyService,
    private val settingsService: SettingsService,
    private val experienceBossBarProvider: ExperienceBossBarProvider,
) : Listener {
    private val config = ConfigManager.get()

    private val statusActionBarProvider =
        StatusActionBarProvider { uuid ->
            moneyService.getBalance(uuid)
        }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        experienceService.loadExperience(player.uniqueId)
        settingsService.loadSettings(player.uniqueId)

        if (settingsService.shouldShowBossBar(player)) {
            BossBarManager.registerProvider(player, experienceBossBarProvider)
        }

        moneyService.loadBalance(player.uniqueId)
        ActionBarManager.registerPersistentProvider(player, statusActionBarProvider)
        player.sendMessage("プレイヤーデータ読み込み完了".infoMessage())

        // 初見時の処理
        if (!player.hasPlayedBefore()) {
            // TODO: チュートリアルの処理を入れる?
            event.joinMessage(Component.text("初参加の ${player.name} がログインしました! ようこそ!").color(NamedTextColor.LIGHT_PURPLE))
            player.sendMessage(config.plugin.firstMotd.infoMessage())
            moneyService.addBalance(player, config.plugin.tutorialRune.toULong())
                ?: player.sendMessage("チュートリアルの際に付与される Rune が正しく入手できませんでした。運営に連絡してください".errorMessage())
            return
        }

        player.sendMessage(config.plugin.motd.systemMessage())
        event.joinMessage("${player.name} がログインしました".systemMessage())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player

        BossBarManager.unregisterProvider(player)
        experienceService.clearCache(player.uniqueId)

        ActionBarManager.unregisterPersistentProvider(player)
        moneyService.clearCache(player.uniqueId)
        settingsService.clearCache(player.uniqueId)

        PlayerHeadCacheManager.invalidateCache(player.uniqueId)

        event.quitMessage("${player.name} がログアウトしました".systemMessage())
    }
}
