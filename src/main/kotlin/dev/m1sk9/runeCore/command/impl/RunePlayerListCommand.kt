package dev.m1sk9.runeCore.command.impl

import dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand
import dev.m1sk9.runeCore.command.register.CommandResult
import dev.m1sk9.runeCore.command.register.RuneCommand
import dev.m1sk9.runeCore.command.register.RuneCommandContext

@PlayerOnlyCommand
class RunePlayerListCommand : RuneCommand {
    override val name = "playerlist"
    override val description = "サーバーに接続しているプレイヤーの一覧を表示します"
    override val aliases = listOf("list", "plist", "players")

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
