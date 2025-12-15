package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.database.repository.PlayerRepository
import dev.m1sk9.runeCore.database.repository.RepositoryResult
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.util.logging.Logger

class PlayerLoginListener(
    private val playerRepository: PlayerRepository,
    private val logger: Logger,
) : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onLogin(event: AsyncPlayerPreLoginEvent) {
        val uuid: String = event.uniqueId.toString()

        when (val result = playerRepository.existsByUUID(uuid)) {
            is RepositoryResult.Success -> {
                if (!result.data) {
                    when (playerRepository.createPlayer(uuid)) {
                        is RepositoryResult.Success -> {
                            logger.info("Created new player data: $uuid")
                        }
                        is RepositoryResult.Error -> {
                            logger.severe("Error creating player data: $uuid")
                        }
                        else -> {}
                    }
                }
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to check player: $uuid")
            }
            else -> {}
        }
    }
}
