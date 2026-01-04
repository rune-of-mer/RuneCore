package org.lyralis.runeCore.command.impl.teleport

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.domain.teleport.TeleportRequestManager

/**
 * /tpc - テレポートリクエストを却下するコマンド
 */
@PlayerOnlyCommand
class RuneTpcCommand(
    private val requestManager: TeleportRequestManager,
) : RuneCommand {
    override val name = "tpc"
    override val description = "テレポートリクエストを却下します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val requests = requestManager.getRequestsFor(player.uniqueId)

        if (requests.isEmpty()) {
            return CommandResult.Failure.Custom("却下できるテレポートリクエストがありません")
        }

        requests.forEach { request ->
            val requester = player.server.getPlayer(request.requesterId)
            requester?.sendMessage("${player.name} がテレポートリクエストを却下しました".errorMessage())
        }

        requestManager.clearRequestsFor(player.uniqueId)

        return CommandResult.Success("${requests.size}件のテレポートリクエストを却下しました")
    }
}
