package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.database.impl.money.MoneyService

@PlayerOnlyCommand
class RunePayCommand(
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "pay"
    override val description = "指定したプレイヤーにお金を支払います。"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val target =
            context.args.getOrNull(0)
                ?: return CommandResult.Failure.InvalidArgument("送金するプレイヤーを指定してください")

        if (player.name == target) {
            return CommandResult.Failure.InvalidArgument("自分自身には送金できません")
        }

        val targetPlayer =
            player.server.getPlayer(target)
                ?: return CommandResult.Failure.InvalidArgument("指定したプレイヤーは存在しません")

        val amountArgs =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.InvalidArgument("送金額を指定してください")

        val amount =
            amountArgs.toULongOrNull()
                ?: return CommandResult.Failure.InvalidArgument("送金額は数値で指定してください")

        if (amount <= 0u) {
            return CommandResult.Failure.InvalidArgument("送金額は0以上で指定してください")
        }

        val result =
            moneyService.transferBalance(player, targetPlayer, amount)
                ?: return CommandResult.Failure.ExecutionFailed("送金処理に失敗しました。送金する金額が手元にあるか確認してください")

        targetPlayer.sendMessage("${player.name} から $amount Rune が送金されました (現在の所持金: ${result.second} Rune)".systemMessage())
        return CommandResult.Success("${targetPlayer.name} に $amount Rune を送金しました (現在の所持金: ${result.first} Rune)")
    }
}
