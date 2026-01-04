package org.lyralis.runeCore.command.impl.money

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.permission.Permission

/**
 * /money コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能．
 */
@PlayerOnlyCommand
@CommandPermission(Permission.Admin.MoneyCommand::class)
class RuneMoneyCommand(
    moneyService: MoneyService,
) : RuneCommand {
    override val name = "money"
    override val description = "所持金を管理します"
    override val aliases = listOf("rune")

    override val subcommands: List<RuneCommand> =
        listOf(
            RuneMoneyAddCommand(moneyService),
            RuneMoneyReduceCommand(moneyService),
            RuneMoneySetCommand(moneyService),
        )

    override fun execute(context: RuneCommandContext): CommandResult =
        CommandResult.Failure.InvalidArgument("/money <set/add/reduce> <金額> [プレイヤー]")

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 -> context.filterStartsWith(listOf("set", "add", "reduce"))
            2 -> {
                return listOf("100", "200", "300", "400", "500")
            }
            3 -> {
                return Bukkit.getOnlinePlayers().map { it.name }
            }
            else -> emptyList()
        }
}
