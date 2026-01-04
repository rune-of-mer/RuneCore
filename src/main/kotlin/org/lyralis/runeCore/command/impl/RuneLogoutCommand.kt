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

@PlayerOnlyCommand
class RuneLogoutCommand : RuneCommand {
    private val config = ConfigManager.get()

    override val name = "logout"
    override val description = "サーバーからログアウトします"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        if (player.isFlying) {
            return CommandResult.Failure.ExecutionFailed("飛行中はログアウトできません")
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
