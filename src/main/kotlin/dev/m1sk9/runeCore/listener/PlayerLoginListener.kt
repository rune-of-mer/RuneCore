package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.component.errorMessage
import dev.m1sk9.runeCore.database.repository.PlayerRepository
import dev.m1sk9.runeCore.database.repository.RepositoryResult
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result
import java.util.logging.Logger

class PlayerLoginListener(
    private val playerRepository: PlayerRepository,
    private val logger: Logger,
) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid = event.uniqueId

        when (val result = playerRepository.existsByUUID(uuid)) {
            is RepositoryResult.Success -> {
                if (!result.data) {
                    when (playerRepository.createPlayer(uuid)) {
                        is RepositoryResult.Success -> {
                            logger.info("Created new player data: $uuid")
                            event.allow()
                        }
                        is RepositoryResult.Error -> {
                            logger.severe("Error creating player data: $uuid")
                            event.disallow(Result.KICK_OTHER, "プレイヤーデータの作成に失敗しました。公式Discordにて運営のサポートを受けてください".errorMessage())
                        }
                        else -> {}
                    }
                }
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to check player: $uuid")
                event.disallow(Result.KICK_OTHER, "プレイヤーデータの作成に失敗しました。公式Discordにて運営のサポートを受けてください".errorMessage())
            }
            else -> {}
        }
    }
}
