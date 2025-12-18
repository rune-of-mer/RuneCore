package org.lyralis.runeCore.database.impl.experience

import org.bukkit.entity.Player
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class ExperienceService(
    private val playerRepository: PlayerRepository,
    private val logger: Logger,
) {
    private val experienceCache = ConcurrentHashMap<UUID, ULong>()
    private val levelCache = ConcurrentHashMap<UUID, UInt>()

    fun grantExperience(
        player: Player,
        amount: ULong,
    ): ULong? {
        if (amount == 0uL) {
            return null
        }

        val uuid = player.uniqueId
        val currentExperience = getExperience(uuid)
        val newExperience = currentExperience + amount
        val oldLevel = getLevel(uuid)
        val newLevel = ExperienceCalculator.calculateLevel(newExperience)

        return when (val result = playerRepository.addExperience(uuid, amount)) {
            is RepositoryResult.Success -> {
                if (newLevel != oldLevel) {
                    playerRepository.setLevel(uuid, newLevel)
                }

                experienceCache[uuid] = newExperience
                levelCache[uuid] = newLevel

                ExperienceBossBarManager.updateBossBar(player, newLevel, newExperience)

                newExperience
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player stats not found for ${player.name}")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to grant experience to ${player.name}: ${result.exception.message}")
                null
            }
            else -> null
        }
    }

    fun getLevel(uuid: UUID): UInt {
        levelCache[uuid]?.let { return it }

        val experience = getExperience(uuid)
        val level = ExperienceCalculator.calculateLevel(experience)

        levelCache[uuid] = level
        return level
    }

    fun isMaxLevel(uuid: UUID): Boolean {
        val level = getLevel(uuid)
        return level >= ExperienceCalculator.getMaxLevel()
    }

    fun initializeBossBar(player: Player) {
        val totalExp = getExperience(player.uniqueId)
        val level = getLevel(player.uniqueId)
        ExperienceBossBarManager.updateBossBar(player, level, totalExp)
    }

    fun cleanupBossBar(player: Player) {
        ExperienceBossBarManager.removeBossBar(player)
    }

    fun setExperience(
        player: Player,
        totalExperience: ULong,
    ): ULong? {
        val validExperience = totalExperience.coerceAtLeast(0uL)
        val uuid = player.uniqueId
        val newLevel = ExperienceCalculator.calculateLevel(validExperience)

        when (playerRepository.setExperience(uuid, validExperience)) {
            is RepositoryResult.Success -> {
                val currentLevel = levelCache[uuid] ?: 1
                if (newLevel != currentLevel) {
                    when (playerRepository.setLevel(uuid, newLevel)) {
                        is RepositoryResult.Success -> {
                            levelCache[uuid] = newLevel
                        }
                        is RepositoryResult.NotFound -> {
                            logger.warning("Attempted to set level for non-existent experience: $uuid")
                            return null
                        }
                        is RepositoryResult.Error -> {
                            logger.severe("Failed to set level for non-existent experience: $uuid")
                            return null
                        }
                        else -> {
                            return null
                        }
                    }
                }

                experienceCache[uuid] = validExperience
                levelCache[uuid] = newLevel

                ExperienceBossBarManager.updateBossBar(player, newLevel, validExperience)
                return validExperience
            }
            else -> return null
        }
    }

    fun getExperience(uuid: UUID): ULong {
        experienceCache[uuid]?.let { return it }

        return when (val result = playerRepository.findByUUID(uuid)) {
            is RepositoryResult.Success -> {
                val experience = result.data.experience
                experienceCache[uuid] = experience
                levelCache[uuid] = result.data.level
                experience
            }
            is RepositoryResult.NotFound -> {
                logger.warning("No experience found for $uuid")
                0uL
            }
            is RepositoryResult.Error -> {
                logger.severe("An error occurred while fetching experience for $uuid")
                0uL
            }
            else -> 0uL
        }
    }

    fun loadExperience(uuid: UUID) {
        when (val result = playerRepository.findByUUID(uuid)) {
            is RepositoryResult.Success -> {
                val data = result.data
                experienceCache[uuid] = data.experience
                levelCache[uuid] = data.level
                logger.info("Loaded experience for $uuid: ${data.experience} EXP, Level ${data.level}")
            }
            is RepositoryResult.NotFound -> {
                logger.info("Player stats not found for $uuid, will be created on first write")
                experienceCache[uuid] = 0uL
                levelCache[uuid] = 1u
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to load experience for $uuid: ${result.exception.message}")
                experienceCache[uuid] = 0uL
                levelCache[uuid] = 1u
            }
            else -> {
                logger.warning("Unexpected result when loading experience for $uuid")
                experienceCache[uuid] = 0uL
                levelCache[uuid] = 1u
            }
        }
    }

    fun saveExperience(uuid: UUID): Boolean {
        val cachedExperience = experienceCache[uuid] ?: return true
        val cachedLevel = levelCache[uuid] ?: return true

        return when (playerRepository.setExperience(uuid, cachedExperience)) {
            is RepositoryResult.Success -> {
                when (playerRepository.setLevel(uuid, cachedLevel)) {
                    is RepositoryResult.Success -> true
                    is RepositoryResult.Error -> {
                        logger.severe("Failed to set level for $uuid: $cachedLevel")
                        false
                    }
                    else -> false
                }
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to set level for $uuid: $cachedLevel")
                false
            }
            else -> false
        }
    }

    fun clearCache(uuid: UUID) {
        experienceCache[uuid] = 0uL
    }

    fun clearAllCache() {
        experienceCache.clear()
        levelCache.clear()
    }
}
