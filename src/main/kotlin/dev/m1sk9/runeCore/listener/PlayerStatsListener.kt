package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.action.PlayerSessionAction
import dev.m1sk9.runeCore.database.repository.RepositoryResult
import dev.m1sk9.runeCore.database.repository.StatsRepository
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.logging.Logger

class PlayerStatsListener(
    private val statsRepo: StatsRepository,
    private val logger: Logger,
) : Listener {
    @EventHandler
    fun onMobKill(event: EntityDeathEvent) {
        if (event.entity is Player) return
        val killer = event.entity.killer ?: return

        when (val result = statsRepo.incrementMobKills(killer.uniqueId)) {
            is RepositoryResult.NotFound -> {
                logger.warning("Stats not found for ${killer.uniqueId}")
            }
            is RepositoryResult.Error -> {
                logger.severe("[Mob Kills] Failed to increment: ${result.exception.message}")
                PlayerSessionAction(killer).setErrorKick()
            }
            else -> {}
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer = event.entity.killer

        when (val result = statsRepo.incrementDeaths(victim.uniqueId)) {
            is RepositoryResult.NotFound -> {
                logger.warning("Stats not found for ${victim.uniqueId}")
            }
            is RepositoryResult.Error -> {
                logger.severe("[Deaths] Failed to increment: ${result.exception.message}")
                PlayerSessionAction(victim).setErrorKick()
            }
            else -> {}
        }

        killer?.let {
            when (val result = statsRepo.incrementKills(it.uniqueId)) {
                is RepositoryResult.NotFound -> {
                    logger.warning("Stats not found for ${it.uniqueId}")
                }
                is RepositoryResult.Error -> {
                    logger.severe("[Kills] Failed to increment: ${result.exception.message}")
                    PlayerSessionAction(it).setErrorKick()
                }
                else -> {}
            }
        }
    }
}
