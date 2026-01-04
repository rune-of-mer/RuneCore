package org.lyralis.runeCore.command.impl.warp

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.database.repository.WarpPointRepository

@PlayerOnlyCommand
class RuneWarpDeleteCommand(
    private val warpPointRepository: WarpPointRepository,
) : RuneCommand {
    override val name = "delete"
    override val description = "ワープポイントを削除します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val warpName =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.InvalidArgument("/warp delete <拠点名>")

        return when (val result = warpPointRepository.deleteByOwnerAndName(player.uniqueId, warpName)) {
            is RepositoryResult.Success -> {
                CommandResult.Success("ワープポイント '$warpName' を削除しました")
            }
            is RepositoryResult.NotFound -> {
                CommandResult.Failure.Custom("ワープポイント '$warpName' が見つかりません")
            }
            is RepositoryResult.Error -> {
                CommandResult.Failure.ExecutionFailed("ワープポイントの削除に失敗しました: ${result.exception.message}")
            }
            else -> CommandResult.Failure.ExecutionFailed("予期しないエラーが発生しました")
        }
    }

    override fun suggest(context: SuggestionContext): List<String> {
        val player = context.player ?: return emptyList()
        return when (context.args.size) {
            1 -> {
                when (val result = warpPointRepository.findAllByOwner(player.uniqueId)) {
                    is RepositoryResult.Success ->
                        result.data
                            .map { it.name }
                            .filter { it.lowercase().startsWith(context.currentArg.lowercase()) }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
    }
}
