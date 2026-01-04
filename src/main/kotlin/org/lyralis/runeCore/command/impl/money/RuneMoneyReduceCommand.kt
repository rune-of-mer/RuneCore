package org.lyralis.runeCore.command.impl.money

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.MoneyReduceCommand::class)
class RuneMoneyReduceCommand(
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "reduce"
    override val description = "指定したプレイヤーの所持金からお金を減らします"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val amount =
            context.arg(1, "500").toULongOrNull()
                ?: return CommandResult.Failure.InvalidArgument("減らす所持金は数値で指定してください")

        if (amount !in 1uL..<1000000uL) {
            return CommandResult.Failure.InvalidArgument("一度に減らせる所持金は1以上1000000未満です")
        }

        val target = context.arg(2, player.name)
        val targetPlayer =
            Bukkit.getServer().getPlayer(target)
                ?: return CommandResult.Failure.ExecutionFailed("指定したプレイヤーは存在しません")

        val newMoney =
            moneyService.subtractBalance(targetPlayer, amount)
                ?: return CommandResult.Failure.ExecutionFailed("所持金の減少処理に失敗しました")

        return CommandResult.Success("$target の所持金から $amount 減らしました (現在の所持金: $newMoney Rune)")
    }
}
