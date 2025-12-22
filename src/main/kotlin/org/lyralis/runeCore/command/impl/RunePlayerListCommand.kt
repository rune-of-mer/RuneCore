package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.template.showPaginatedGui
import org.lyralis.runeCore.gui.toCommandResult
import java.util.UUID

/**
 * /playerlist コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能
 */
@PlayerOnlyCommand
class RunePlayerListCommand(
    private val playerRepository: PlayerRepository,
) : RuneCommand {
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

        return player
            .showPaginatedGui {
                title = "プレイヤー一覧 - ${onlinePlayers.size}/${maxPlayers}人"
                itemsPerPage = 10

                items(onlinePlayers)

                render { player ->
                    player.getCachedPlayerHead {
                        displayName = "${player.name} - Lv${getLevel(player.uniqueId) ?: 0}"
                        lore {
                            +"UUID: ${player.uniqueId}"
                            +"Ping値: ${player.ping}ms"
                            +"現在地: ${player.world.name}"
                        }
                    }
                }
            }.toCommandResult("現在 ${onlinePlayers.size} 人がオンラインです (最大 $maxPlayers 人)")
    }

    private fun getLevel(uuid: UUID): UInt? =
        when (val result = playerRepository.getLevel(uuid)) {
            is RepositoryResult.Success -> {
                result.data
            }
            else -> null
        }
}
