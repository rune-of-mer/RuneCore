package org.lyralis.runeCore.command.impl.warp.admin

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.database.repository.WarpPointRepository
import org.lyralis.runeCore.permission.Permission

@CommandPermission(Permission.Admin.WarpAddPoint::class)
class RuneWarpAddPointSubCommand(
    private val warpPointRepository: WarpPointRepository,
) : RuneCommand {
    override val name = "point"
    override val description = "プレイヤーのワープスロットを追加します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val targetName =
            context.args.getOrNull(0)
                ?: return CommandResult.Failure.InvalidArgument("/warp add point <プレイヤー> <数値>")

        val amount =
            context.args.getOrNull(1)?.toIntOrNull()
                ?: return CommandResult.Failure.InvalidArgument("スロット数は数値で指定してください")

        if (amount <= 0) {
            return CommandResult.Failure.InvalidArgument("スロット数は1以上で指定してください")
        }

        val target = Bukkit.getOfflinePlayer(targetName)
        if (!target.hasPlayedBefore() && !target.isOnline) {
            return CommandResult.Failure.Custom("プレイヤー '$targetName' が見つかりません")
        }

        return when (val result = warpPointRepository.addSlots(target.uniqueId, amount)) {
            is RepositoryResult.Success -> {
                CommandResult.Success("${target.name} のワープスロットを $amount 追加しました (追加スロット合計: ${result.data})")
            }
            is RepositoryResult.Error -> {
                CommandResult.Failure.ExecutionFailed("スロット追加に失敗しました: ${result.exception.message}")
            }
            else -> CommandResult.Failure.ExecutionFailed("予期しないエラーが発生しました")
        }
    }

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 ->
                Bukkit
                    .getOnlinePlayers()
                    .map { it.name }
                    .filter { it.lowercase().startsWith(context.currentArg.lowercase()) }
            2 -> listOf("1", "2", "3", "5", "10")
            else -> emptyList()
        }
}
