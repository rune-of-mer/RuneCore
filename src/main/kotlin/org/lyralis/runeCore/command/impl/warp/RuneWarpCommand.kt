package org.lyralis.runeCore.command.impl.warp

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.impl.warp.admin.RuneWarpAddCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.config.model.TeleportConfig
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.impl.teleport.TeleportCostCalculator
import org.lyralis.runeCore.database.repository.WarpPointRepository
import org.lyralis.runeCore.teleport.TeleportService

/**
 * /warp - ワープポイントを管理する親コマンド
 */
@PlayerOnlyCommand
class RuneWarpCommand(
    warpPointRepository: WarpPointRepository,
    teleportService: TeleportService,
    costCalculator: TeleportCostCalculator,
    moneyService: MoneyService,
    config: TeleportConfig,
) : RuneCommand {
    override val name = "warp"
    override val description = "ワープポイントを管理します"

    override val subcommands: List<RuneCommand> =
        listOf(
            RuneWarpCreateCommand(warpPointRepository, config),
            RuneWarpGoCommand(warpPointRepository, teleportService, costCalculator, moneyService),
            RuneWarpDeleteCommand(warpPointRepository),
            RuneWarpListCommand(warpPointRepository, teleportService, costCalculator, moneyService, config),
            RuneWarpAddCommand(warpPointRepository),
        )

    override fun execute(context: RuneCommandContext): CommandResult =
        CommandResult.Failure.InvalidArgument("/warp <create|go|delete|list|add>")

    override fun suggest(context: SuggestionContext): List<String> =
        context.filterStartsWith(listOf("create", "go", "delete", "list", "add"))
}
