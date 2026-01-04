package org.lyralis.runeCore.command.impl.teleport

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.teleport.TeleportRequestManager
import org.lyralis.runeCore.domain.teleport.TeleportService
import org.lyralis.runeCore.gui.impl.teleport.TeleportConfirmGui
import org.lyralis.runeCore.gui.impl.teleport.TeleportRequestSelectGui

/**
 * /tpa - テレポートリクエストを承認するコマンド
 */
@PlayerOnlyCommand
class RuneTpaCommand(
    private val requestManager: TeleportRequestManager,
    private val teleportService: TeleportService,
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "tpa"
    override val description = "テレポートリクエストを承認します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val requests = requestManager.getRequestsFor(player.uniqueId)

        if (requests.isEmpty()) {
            return CommandResult.Failure.Custom("承認待ちのテレポートリクエストがありません")
        }

        if (requests.size == 1) {
            val request = requests.first()
            TeleportConfirmGui(requestManager, teleportService, moneyService, request).open(player)
        } else {
            TeleportRequestSelectGui(requestManager, teleportService, moneyService, requests).open(player)
        }

        return CommandResult.Silent
    }
}
