package org.lyralis.runeCore.listener.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.domain.player.PlayerService
import java.util.logging.Logger

class PlayerLoginListener(
    private val playerService: PlayerService,
    private val logger: Logger,
) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId

        if (!playerService.existsPlayer(uuid)) {
            if (playerService.createPlayer(uuid)) {
                logger.info("Created new player data: $uuid")
                event.allow()
            } else {
                logger.severe("Error creating player data: $uuid")
                event.disallow(Result.KICK_OTHER, "プレイヤーデータの作成に失敗しました。公式Discordにて運営のサポートを受けてください".errorMessage())
            }
        } else {
            logger.info("Player $uuid successfully logged in.")
        }
    }
}
