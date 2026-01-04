package org.lyralis.runeCore.command.impl.warp

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.config.model.TeleportConfig
import org.lyralis.runeCore.database.repository.WarpPointRepository
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.teleport.TeleportCostCalculator
import org.lyralis.runeCore.domain.teleport.TeleportService
import org.lyralis.runeCore.gui.impl.teleport.WarpPointListGui

/**
 * /warp list - ワープポイント一覧をGUIで表示するコマンド
 */
@PlayerOnlyCommand
class RuneWarpListCommand(
    private val warpPointRepository: WarpPointRepository,
    private val teleportService: TeleportService,
    private val costCalculator: TeleportCostCalculator,
    private val moneyService: MoneyService,
    private val config: TeleportConfig,
) : RuneCommand {
    override val name = "list"
    override val description = "ワープポイント一覧を表示します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        WarpPointListGui(
            warpPointRepository,
            teleportService,
            costCalculator,
            moneyService,
            config,
        ).open(player)

        return CommandResult.Silent
    }
}
