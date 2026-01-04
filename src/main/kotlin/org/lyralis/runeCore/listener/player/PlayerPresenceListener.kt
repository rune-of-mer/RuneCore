package org.lyralis.runeCore.listener.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
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
import org.lyralis.runeCore.domain.experience.ExperienceService
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.settings.SettingsService
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

        moneyService.loadBalance(player.uniqueId)
        ActionBarManager.registerPersistentProvider(player, statusActionBarProvider)
        player.sendMessage("プレイヤーデータの読み込みが完了しました".infoMessage())

        if (settingsService.shouldShowBossBar(player)) {
            BossBarManager.registerProvider(player, experienceBossBarProvider)
        } else {
            player.apply {
                sendMessage(
                    "現在経験値バーが非表示になっています。このメッセージをクリックすると表示することができます"
                        .infoMessage()
                        .clickEvent(ClickEvent.runCommand("/settings bossbar")),
                )
            }
        }

        // 初見時の処理
        if (!player.hasPlayedBefore()) {
            // TODO: チュートリアルの処理を入れる?
            event.joinMessage(Component.text("初参加の ${player.name} がログインしました! ようこそ!").color(NamedTextColor.LIGHT_PURPLE))
            player.sendMessage(Component.text(config.plugin.firstMotd.joinToString("\n"), NamedTextColor.LIGHT_PURPLE))
            moneyService.addBalance(player, config.plugin.tutorialRune.toULong())
                ?: player.sendMessage("チュートリアルの際に付与される Rune が正しく入手できませんでした。運営に連絡してください".errorMessage())
            return
        }

        player.showTitle(
            Title.title(
                Component.text("Welcome to Rune of Mer!", NamedTextColor.LIGHT_PURPLE),
                Component.empty(),
            ),
        )

        player.sendMessage(Component.text(config.plugin.motd.joinToString("\n"), NamedTextColor.LIGHT_PURPLE))
        event.joinMessage("[Join] ${player.name} がログインしました".systemMessage())
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

        event.quitMessage("[Quit] ${player.name} がログアウトしました".systemMessage())
    }
}
