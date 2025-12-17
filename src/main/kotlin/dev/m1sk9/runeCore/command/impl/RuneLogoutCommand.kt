package dev.m1sk9.runeCore.command.impl

import dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand
import dev.m1sk9.runeCore.command.register.CommandResult
import dev.m1sk9.runeCore.command.register.RuneCommand
import dev.m1sk9.runeCore.command.register.RuneCommandContext
import dev.m1sk9.runeCore.component.systemMessage

@PlayerOnlyCommand
class RuneLogoutCommand : RuneCommand {
    override val name = "logout"
    override val description = "サーバーからログアウトします"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        if (player.isFlying) {
            return CommandResult.Failure.ExecutionFailed("飛行中はログアウトできません")
        }

        player.kick("コマンドによりログアウトしました".systemMessage())
        return CommandResult.Silent
    }
}
