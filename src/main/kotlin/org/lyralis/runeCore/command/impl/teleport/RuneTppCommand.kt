package org.lyralis.runeCore.command.impl.teleport

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.impl.teleport.TeleportCostCalculator
import org.lyralis.runeCore.database.model.teleport.TeleportRequest
import org.lyralis.runeCore.teleport.TeleportRequestManager

/**
 * /tpp <プレイヤー> - テレポートリクエストを送信するコマンド
 */
@PlayerOnlyCommand
class RuneTppCommand(
    private val requestManager: TeleportRequestManager,
    private val costCalculator: TeleportCostCalculator,
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "tpp"
    override val description = "プレイヤーへのテレポートリクエストを送信します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val targetName =
            context.args.getOrNull(0)
                ?: return CommandResult.Failure.InvalidArgument("/tpp <プレイヤー名>")

        val target =
            player.server.getPlayer(targetName)
                ?: return CommandResult.Failure.Custom("プレイヤー '$targetName' が見つかりません")

        if (target.uniqueId == player.uniqueId) {
            return CommandResult.Failure.Custom("自分自身にはテレポートリクエストを送れません")
        }

        val cost = costCalculator.calculateCost(player.location, target.location)

        // 所持金チェック
        val balance = moneyService.getBalance(player.uniqueId)
        if (balance < cost) {
            return CommandResult.Failure.Custom(
                "所持金が足りません (所持金: $balance Rune / 必要: $cost Rune)",
            )
        }

        val request =
            TeleportRequest(
                requesterId = player.uniqueId,
                requesterName = player.name,
                targetId = target.uniqueId,
                requesterLocation = player.location,
                cost = cost,
            )

        requestManager.addRequest(request)

        target.sendMessage("${player.name} からテレポートリクエストが届きました (料金: $cost Rune)".systemMessage())
        target.sendMessage("/tpa で承認、/tpc で却下できます".infoMessage())

        return CommandResult.Success("${target.name} にテレポートリクエストを送信しました (料金: $cost Rune)")
    }

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 ->
                Bukkit
                    .getOnlinePlayers()
                    .filter { it.uniqueId != context.player?.uniqueId }
                    .map { it.name }
                    .filter { it.lowercase().startsWith(context.currentArg.lowercase()) }
            else -> emptyList()
        }
}
