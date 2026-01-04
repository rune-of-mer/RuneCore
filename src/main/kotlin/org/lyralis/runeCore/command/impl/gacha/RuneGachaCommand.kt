package org.lyralis.runeCore.command.impl.gacha

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.domain.gacha.GachaService
import org.lyralis.runeCore.gui.impl.gacha.GachaDetailGui
import org.lyralis.runeCore.gui.impl.gacha.GachaListGui
import org.lyralis.runeCore.gui.toCommandResult

/**
 * /gacha コマンド - ガチャGUIを開く
 *
 * - `/gacha` - ガチャ一覧GUIを開く
 * - `/gacha <event_id>` - 指定したガチャの詳細GUIを直接開く
 */
@PlayerOnlyCommand
class RuneGachaCommand(
    private val gachaService: GachaService,
) : RuneCommand {
    override val name = "gacha"
    override val description = "ガチャを開きます"

    private val listGui by lazy { GachaListGui(gachaService) }

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val eventId = context.args.getOrNull(0) ?: return listGui.open(player).toCommandResult()

        val event =
            gachaService.getEventById(eventId)
                ?: return CommandResult.Failure.Custom("ガチャイベント '$eventId' が見つかりません")

        if (!event.isActive) {
            return CommandResult.Failure.Custom("ガチャイベント '${event.displayName}' は現在開催されていません")
        }

        return GachaDetailGui(gachaService, listGui).open(player, eventId).toCommandResult()
    }

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 ->
                gachaService
                    .getActiveEvents()
                    .map { it.id }
                    .filter { it.startsWith(context.currentArg.lowercase()) }
            else -> emptyList()
        }
}
