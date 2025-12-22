package org.lyralis.runeCore.command.impl.money

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.MoneySetCommand::class)
class RuneMoneySetCommand(
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "set"
    override val description = "指定したプレイヤーの所持金をセットします"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val amount =
            context.arg(1, "500").toULongOrNull()
                ?: return CommandResult.Failure.InvalidArgument("セットする所持金は数値で指定してください")

        if (amount !in 1uL..<1000000uL) {
            return CommandResult.Failure.InvalidArgument("一度にセットできる所持金は1以上1000000未満です")
        }

        val target = context.arg(2, player.name)
        val targetPlayer =
            Bukkit.getServer().getPlayer(target)
                ?: return CommandResult.Failure.ExecutionFailed("指定したプレイヤーは存在しません")

        val newMoney =
            moneyService.setBalance(targetPlayer, amount)
                ?: return CommandResult.Failure.ExecutionFailed("所持金のセットに失敗しました")

        return CommandResult.Success("$target の所持金を $newMoney Rune にセットしました")
    }
}
