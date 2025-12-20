package org.lyralis.runeCore.command.impl

import org.bukkit.entity.Player
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.database.impl.experience.ExperienceCalculator
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import java.util.logging.Logger

/**
 * /level コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能．
 */
@PlayerOnlyCommand
class RuneLevelCommand(
    private val playerRepository: PlayerRepository,
    private val logger: Logger
): RuneCommand {

    override val name = "level"
    override val description = "現在のレベルを確認します"
    override val aliases = listOf("lv")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val levelInfo = getLevelInfo(player)
            ?: return CommandResult.Failure.ExecutionFailed("レベル情報の取得に失敗しました")
        val maxLevel = ExperienceCalculator.getMaxLevel()

        // TODO: 最大レベルの場合，経験値量の処理はどうするか
        val result = if (levelInfo.third >= maxLevel) {
            listOf(
                "現在のレベル情報:",
                "   最大レベルに到達しています",
                "   経験値量は最大レベルの上限突破後にレベルアップに使用されます",
                "   レベル: ${levelInfo.third}",
                "   経験値: ${levelInfo.first}",
            ).joinToString("\n")
        } else {
            listOf(
                "現在のレベル情報:",
                "   レベル: ${levelInfo.third}/${maxLevel}",
                "   経験値: ${levelInfo.first}/${levelInfo.second}",
            ).joinToString("\n")
        }

        return CommandResult.Success(result)
    }

    // 総経験値/次までの経験値/現在のレベル
    private fun getLevelInfo(player: Player): Triple<ULong, ULong, UInt>? {
        when (val result = playerRepository.getExperience(player.uniqueId)) {
            is RepositoryResult.Success -> {
                val totalExperience = result.data
                val currentLevel = ExperienceCalculator.calculateLevel(totalExperience)
                val requiredExperience = ExperienceCalculator.getExperienceForLevel(currentLevel)

                return Triple(totalExperience, requiredExperience, currentLevel)
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player stats not found for ${player.name}")
                return null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to grant experience to ${player.name}: ${result.exception.message}")
                return null
            }
            else -> return null
        }
        return null
    }
}
