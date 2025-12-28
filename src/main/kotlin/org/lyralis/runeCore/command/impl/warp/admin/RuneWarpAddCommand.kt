package org.lyralis.runeCore.command.impl.warp.admin

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.database.repository.WarpPointRepository
import org.lyralis.runeCore.permission.Permission

/**
 * /warp add - 管理者用のワープ関連追加コマンド（親コマンド）
 */
@CommandPermission(Permission.Admin.WarpAddPoint::class)
class RuneWarpAddCommand(
    warpPointRepository: WarpPointRepository,
) : RuneCommand {
    override val name = "add"
    override val description = "管理者用のワープポイント関連コマンド"

    override val subcommands: List<RuneCommand> =
        listOf(
            RuneWarpAddPointSubCommand(warpPointRepository),
        )

    override fun execute(context: RuneCommandContext): CommandResult = CommandResult.Failure.InvalidArgument("/warp add point <プレイヤー> <数値>")

    override fun suggest(context: SuggestionContext): List<String> = context.filterStartsWith(listOf("point"))
}
