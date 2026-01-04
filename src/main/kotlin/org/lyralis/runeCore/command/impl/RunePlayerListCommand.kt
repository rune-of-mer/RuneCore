package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.domain.experience.ExperienceCalculator
import org.lyralis.runeCore.domain.player.PlayerService
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.template.showPaginatedGui
import org.lyralis.runeCore.gui.toCommandResult

@PlayerOnlyCommand
class RunePlayerListCommand(
    private val playerService: PlayerService,
) : RuneCommand {
    override val name = "playerlist"
    override val description = "サーバーに接続しているプレイヤーの一覧を表示します"
    override val aliases = listOf("list", "plist", "players")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        // TODO: Should OP players be excluded from the list?

        val onlinePlayers =
            player.server.onlinePlayers.toList()
        val maxPlayers = player.server.maxPlayers

        return player
            .showPaginatedGui {
                title = "プレイヤー一覧 - ${onlinePlayers.size}/${maxPlayers}人"
                itemsPerPage = 10

                items(onlinePlayers)

                render { player ->
                    val level = playerService.getLevel(player.uniqueId)
                    player.getCachedPlayerHead {
                        displayName = player.name
                        lore {
                            +""
                            +"§7レベル: §e$level §7/ §e${ExperienceCalculator.getMaxLevel()}"
                            +""
                            +"§8UUID: §7${player.uniqueId}"
                            +"§8Ping値: §7${player.ping}ms"
                            +"§8現在地: §7${player.world.name}"
                        }
                    }
                }
            }.toCommandResult("現在 ${onlinePlayers.size} 人がオンラインです (最大 $maxPlayers 人)")
    }
}
