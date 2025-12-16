package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.action.PlayerSessionAction
import dev.m1sk9.runeCore.database.repository.RepositoryResult
import dev.m1sk9.runeCore.database.repository.StatsRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import java.util.logging.Logger

class PlayerStatsListener(
    private val statsRepo: StatsRepository,
    private val logger: Logger,
) : Listener {
    // モブを殺害した (mobKills)
    @EventHandler
    fun onMobKill(event: EntityDeathEvent) {
        val target = event.entity.killer ?: return

        when (statsRepo.findByUuid(target.uniqueId)) {
            is RepositoryResult.Success -> {
                when (statsRepo.incrementMobKills(target.uniqueId)) {
                    is RepositoryResult.Success -> {
                        logger.info("[Mob Kills] Successfully incremented ${target.uniqueId}")
                    }
                    is RepositoryResult.Error -> {
                        logger.severe("[Mob Kills] Failed to increment mob kills.")
                        PlayerSessionAction(target).setErrorKick()
                    }
                    else -> {}
                }
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to retrieve stats for ${target.uniqueId}")
                PlayerSessionAction(target).setErrorKick()
            }
            else -> {}
        }
    }
}
