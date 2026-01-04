package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.utils.getFormattedPlayTime

@PlayerOnlyCommand
class RunePlayTimeCommand : RuneCommand {
    override val name = "playtime"
    override val description = "現在の累積プレイ時間を表示します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val result = player.getFormattedPlayTime()

        return CommandResult.Success("現在の累積プレイ時間: $result")
    }
}
