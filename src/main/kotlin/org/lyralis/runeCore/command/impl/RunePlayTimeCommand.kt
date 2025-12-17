package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext

/**
 * /playtime コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能
 */
@PlayerOnlyCommand
class RunePlayTimeCommand : RuneCommand {
    override val name = "playtime"
    override val description = "現在の累積プレイ時間を表示します"

    /**
     * プレイ時間表示コマンドを実行する．
     *
     * @param context コマンドのコンテキスト情報
     * @return コマンドの実行結果
     */
    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        // WARN: playerTime (getPlayerTime()) はティック数なので変換が必要 (秒 → 分 → 時間)
        val playTimeTicks: Long = player.playerTime

        val seconds = playTimeTicks / 20
        val minutes = seconds / 60
        val hours = minutes / 60
        val formatted = String.format("%02d時間%02d分%02d秒", hours, minutes, seconds)

        return CommandResult.Success("現在の累積プレイ時間: $formatted")
    }
}
