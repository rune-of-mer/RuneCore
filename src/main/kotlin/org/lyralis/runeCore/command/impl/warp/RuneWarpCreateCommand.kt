package org.lyralis.runeCore.command.impl.warp

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.config.model.TeleportConfig
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.database.repository.WarpPointRepository

@PlayerOnlyCommand
class RuneWarpCreateCommand(
    private val warpPointRepository: WarpPointRepository,
    private val config: TeleportConfig,
) : RuneCommand {
    override val name = "create"
    override val description = "現在地をワープポイントとして登録します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val warpName =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.InvalidArgument("/warp create <拠点名>")

        if (warpName.length > 32) {
            return CommandResult.Failure.Custom("拠点名は32文字以内で指定してください")
        }

        if (!warpName.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
            return CommandResult.Failure.Custom("拠点名には英数字、ハイフン、アンダースコアのみ使用できます")
        }

        if (warpPointRepository.exists(player.uniqueId, warpName)) {
            return CommandResult.Failure.Custom("'$warpName' という名前のワープポイントは既に存在します")
        }

        val totalSlots =
            when (val result = warpPointRepository.getTotalSlots(player.uniqueId, config.defaultWarpSlots)) {
                is RepositoryResult.Success -> result.data
                else -> config.defaultWarpSlots
            }

        val currentCount =
            when (val result = warpPointRepository.countByOwner(player.uniqueId)) {
                is RepositoryResult.Success -> result.data
                else -> 0
            }

        if (currentCount >= totalSlots) {
            return CommandResult.Failure.Custom("ワープポイントの登録上限（$totalSlots 個）に達しています")
        }

        return when (val result = warpPointRepository.createWarpPoint(player.uniqueId, warpName, player.location)) {
            is RepositoryResult.Success -> {
                val remaining = totalSlots - currentCount - 1
                CommandResult.Success("ワープポイント '$warpName' を登録しました (残りスロット: $remaining)")
            }
            is RepositoryResult.Error -> {
                CommandResult.Failure.ExecutionFailed("ワープポイントの登録に失敗しました: ${result.exception.message}")
            }
            else -> CommandResult.Failure.ExecutionFailed("予期しないエラーが発生しました")
        }
    }
}
