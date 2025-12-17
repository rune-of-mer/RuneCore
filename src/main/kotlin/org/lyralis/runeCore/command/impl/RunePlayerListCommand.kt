package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext

/**
 * /playerlist コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能
 */
@PlayerOnlyCommand
class RunePlayerListCommand : RuneCommand {
    override val name = "playerlist"
    override val description = "サーバーに接続しているプレイヤーの一覧を表示します"
    override val aliases = listOf("list", "plist", "players")

    /**
     * プレイヤーリスト表示コマンドを実行する．
     *
     * @param context コマンドのコンテキスト情報
     * @return コマンドの実行結果
     */
    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        // TODO: Should OP players be excluded from the list?
        val onlinePlayers =
            player.server.onlinePlayers
                .toList()
        val maxPlayers = player.server.maxPlayers

        val result =
            listOf(
                "現在 ${onlinePlayers.size} 人がオンラインです (最大 $maxPlayers 人)",
                onlinePlayers.joinToString(", ") { it.name },
            ).joinToString("\n")

        return CommandResult.Success(result)
    }
}
