package org.lyralis.runeCore.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.actionbar.StatusActionBarProvider
import org.lyralis.runeCore.database.impl.experience.ExperienceBossBarManager
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.gui.cache.PlayerHeadCacheManager
import org.lyralis.runeCore.utils.systemMessage

class PlayerPresenceListener(
    private val experienceService: ExperienceService,
    private val moneyService: MoneyService,
) : Listener {
    private val statusActionBarProvider =
        StatusActionBarProvider { uuid ->
            moneyService.getBalance(uuid)
        }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        experienceService.loadExperience(player.uniqueId)
        experienceService.initializeBossBar(player)

        moneyService.loadBalance(player.uniqueId)
        ActionBarManager.registerPersistentProvider(player, statusActionBarProvider)

        event.joinMessage("${player.name} がログインしました".systemMessage())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player

        ExperienceBossBarManager.removeBossBar(player)
        experienceService.clearCache(player.uniqueId)

        ActionBarManager.unregisterPersistentProvider(player)
        moneyService.clearCache(player.uniqueId)

        PlayerHeadCacheManager.invalidateCache(player.uniqueId)

        event.quitMessage("${player.name} がログアウトしました".systemMessage())
    }
}
