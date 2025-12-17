package dev.m1sk9.runeCore.command.impl

import dev.m1sk9.runeCore.command.RuneCommand
import dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand
import dev.m1sk9.runeCore.command.register.CommandResult
import dev.m1sk9.runeCore.command.register.RuneCommandContext
import dev.m1sk9.runeCore.component.systemMessage

/**
 * /logout コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [dev.m1sk9.runeCore.command] で確認可能．
 */
@PlayerOnlyCommand
class RuneLogoutCommand : RuneCommand {
    override val name = "logout"
    override val description = "サーバーからログアウトします"

    /**
     * ログアウトコマンドを実行する．
     *
     * @param context コマンドのコンテキスト情報
     * @return コマンドの実行結果
     */
    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        if (player.isFlying) {
            return CommandResult.Failure.ExecutionFailed("飛行中はログアウトできません")
        }

        player.kick("コマンドによりログアウトしました".systemMessage())
        return CommandResult.Silent
    }
}
