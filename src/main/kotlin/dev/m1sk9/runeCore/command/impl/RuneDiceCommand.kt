package dev.m1sk9.runeCore.command.impl

import dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand
import dev.m1sk9.runeCore.command.register.CommandResult
import dev.m1sk9.runeCore.command.register.RuneCommand
import dev.m1sk9.runeCore.command.register.RuneCommandContext
import dev.m1sk9.runeCore.component.systemMessage
import org.bukkit.Bukkit

@PlayerOnlyCommand
class RuneDiceCommand : RuneCommand {
    override val name = "dice"
    override val description = "0~999の数字の1つをランダムに抽選しチャットに送信する"

    override fun execute(context: RuneCommandContext): CommandResult {
        val maxNumberStr = context.arg(0, "999")
        val isBroadcastStr = context.arg(1, "true")

        val maxNumber =
            try {
                maxNumberStr.toInt()
            } catch (_: NumberFormatException) {
                return CommandResult.Failure.InvalidArgument("最大値は数値で指定してください: $maxNumberStr")
            }

        if (maxNumber !in 0..<1000) {
            return CommandResult.Failure.InvalidArgument("抽選する数の最大値は0~999の範囲で指定してください")
        }

        val isBroadcast =
            when (isBroadcastStr.lowercase()) {
                "true", "1", "yes" -> true
                "false", "0", "no" -> false
                else -> return CommandResult.Failure.InvalidArgument("ブロードキャストフラグは true/false で指定してください: $isBroadcastStr")
            }

        val result = (0..maxNumber).random()

        return when (isBroadcast) {
            true -> {
                Bukkit.broadcast("${context.playerOrThrow.name} がダイスを振りました。出た目は $result です".systemMessage())
                CommandResult.Silent
            }
            false -> {
                CommandResult.Success("ダイスを振りました。出た目は $result です")
            }
        }
    }
}
