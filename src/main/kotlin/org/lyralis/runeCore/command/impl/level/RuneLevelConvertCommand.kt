package org.lyralis.runeCore.command.impl.level

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.gui.result.ConfirmationResult
import org.lyralis.runeCore.gui.template.showConfirmation
import org.lyralis.runeCore.gui.toCommandResult

@PlayerOnlyCommand
class RuneLevelConvertCommand(
    private val moneyService: MoneyService,
    private val experienceService: ExperienceService,
) : RuneCommand {
    override val name = "convert"
    override val description = "お金から経験値へ変換します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val amountArgs =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.InvalidArgument("変換したい金額を指定してください")
        val convertBalance =
            amountArgs.toULongOrNull()
                ?: return CommandResult.Failure.InvalidArgument("変換金額は数値で指定してください")

        if (convertBalance !in 1u..100000u) {
            return CommandResult.Failure.InvalidArgument("一度に変換できる経験値は1〜10万までです")
        }

        val currentBalance = moneyService.getBalance(player.uniqueId)
        if (convertBalance > currentBalance) {
            return CommandResult.Failure.ExecutionFailed("所持金が不足しています")
        }

        return player
            .showConfirmation {
                title = "最終確認"
                message = "$convertBalance Rune を経験値に変換します。一度変換したお金は返ってきません。よろしいですか?"
                confirmText = "変換する"

                onResult { result ->
                    when (result) {
                        ConfirmationResult.Confirmed -> {
                            val result = experienceService.grantExperience(player, convertBalance)
                            moneyService.subtractBalance(player, convertBalance)
                            player.sendMessage("$convertBalance Runeを経験値に変換しました (現在の総経験値: $result Exp)".systemMessage())
                        }
                        ConfirmationResult.Denied -> {
                            player.sendMessage("変換をキャンセルしました".infoMessage())
                        }
                        ConfirmationResult.Cancelled -> player.closeInventory()
                    }
                }
            }.toCommandResult()
    }
}
