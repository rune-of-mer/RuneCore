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
@CommandPermission(Permission.Admin.MoneyAddCommand::class)
class RuneMoneyAddCommand(
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "add"
    override val description = "指定したプレイヤーの所持金にお金を追加します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val amount =
            context.arg(1, "500").toULongOrNull()
                ?: return CommandResult.Failure.InvalidArgument("セットするお金は数値で指定してください")

        if (amount !in 1uL..<1000000uL) {
            return CommandResult.Failure.InvalidArgument("一度にセットできるお金は1以上1000000未満です")
        }

        val target = context.arg(2, player.name)
        val targetPlayer =
            Bukkit.getServer().getPlayer(target)
                ?: return CommandResult.Failure.ExecutionFailed("指定したプレイヤーは存在しません")

        val newMoney =
            moneyService.addBalance(targetPlayer, amount)
                ?: return CommandResult.Failure.ExecutionFailed("お金の追加に失敗しました")

        return CommandResult.Success("$target の所持金に $amount Rune を追加しました (現在の所持金: $newMoney Rune)")
    }
}
