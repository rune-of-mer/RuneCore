package org.lyralis.runeCore.command.impl

import org.bukkit.Material
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.gui.result.ConfirmationResult
import org.lyralis.runeCore.gui.template.showConfirmation
import org.lyralis.runeCore.gui.toCommandResult
import org.lyralis.runeCore.world.RuneWorldUtils

/**
 * /logout コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能．
 */
@PlayerOnlyCommand
class RuneLogoutCommand : RuneCommand {
    private val config = ConfigManager.get()

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

        if (RuneWorldUtils.isExecute(player.world)) {
            return CommandResult.Failure.ExecutionFailed("ダークゾーン(DZ)にいる間はログアウトできません。先に脱出する必要があります")
        }

        return player
            .showConfirmation {
                title = "ログアウト確認"
                message = "Rune of Mer からログアウトします。よろしいですか？"
                confirmText = "ログアウトする"
                confirmMaterial = Material.DARK_OAK_DOOR
                denyText = "キャンセル"
                denyMaterial = Material.BARRIER

                onResult { result ->
                    when (result) {
                        ConfirmationResult.Confirmed -> player.kick("コマンドによりログアウトしました".systemMessage())
                        ConfirmationResult.Denied -> player.sendMessage("ログアウトをキャンセルしました".systemMessage())
                        else -> player.closeInventory()
                    }
                }
            }.toCommandResult()
    }
}
