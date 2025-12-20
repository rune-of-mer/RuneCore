package org.lyralis.runeCore.database.impl.experience

import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.infoMessage
import org.lyralis.runeCore.component.systemMessage
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class ExperienceService(
    private val playerRepository: PlayerRepository,
    private val logger: Logger,
) {
    private val experienceCache = ConcurrentHashMap<UUID, ULong>()
    private val levelCache = ConcurrentHashMap<UUID, UInt>()

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
                notifyGetExperience(player, amount, newLevel, oldLevel)

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

    /**
     * 現在の UUID に保存されているレベルを返します
     *
     * @param uuid 対象の UUID
     * @return 保存されているレベル
     */
    fun getLevel(uuid: UUID): UInt {
        levelCache[uuid]?.let { return it }

        val experience = getExperience(uuid)
        val level = ExperienceCalculator.calculateLevel(experience)

        levelCache[uuid] = level
        return level
    }

    /**
     * 経験値ボスバーを初期化します．
     *
     * @param player プレイヤー
     */
    fun initializeBossBar(player: Player) {
        val totalExp = getExperience(player.uniqueId)
        val level = getLevel(player.uniqueId)
        ExperienceBossBarManager.updateBossBar(player, level, totalExp)
    }

    /**
     * 現在の総経験値を取得する
     *
     * @param uuid UUID
     * @return 現在の総経験値
     */
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

    /**
     * 現在の総経験値を読み込みます
     *
     * @param uuid UUID
     */
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

    /**
     * キャッシュを初期化します
     *
     * @param uuid UUID
     */
    fun clearCache(uuid: UUID) {
        experienceCache[uuid] = 0uL
        levelCache[uuid] = 1u
    }

    /**
     * 全てのキャッシュを初期化します
     */
    fun clearAllCache() {
        experienceCache.clear()
        levelCache.clear()
    }

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
            sendActionBar("+$addedExperience EXP".infoMessage())
            playSound(sound)
        }
    }
}
