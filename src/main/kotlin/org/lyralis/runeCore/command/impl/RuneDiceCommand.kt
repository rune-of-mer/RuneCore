package org.lyralis.runeCore.command.impl

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.component.message.systemMessage

/**
 * /dice コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能．
 */
@PlayerOnlyCommand
class RuneDiceCommand : RuneCommand {
    override val name = "dice"
    override val description = "0~999の数字の1つをランダムに抽選しチャットに送信する"

    /**
     * ダイスコマンドを実行する．
     *
     * @param context コマンドのコンテキスト情報
     * @return コマンドの実行結果
     */
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
