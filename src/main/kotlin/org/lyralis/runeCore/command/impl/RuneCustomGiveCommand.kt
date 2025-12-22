package org.lyralis.runeCore.command.impl

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.gui.template.showPaginatedGui
import org.lyralis.runeCore.gui.toCommandResult
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.CustomGiveCommand::class)
class RuneCustomGiveCommand : RuneCommand {
    override val name = "givec"
    override val description = "カスタムアイテムをインベントリに追加します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        return player
            .showPaginatedGui {
                title = "カスタムアイテム一覧"
                itemsPerPage = 28

                items(ItemRegistry.getAllItems())

                render { customItem ->
                    customItem.createItemStack()
                }

                onItemClick { item, action ->
                    if (!action.isLeftClick) {
                        GuiResult.Silent
                    } else if (player.inventory.firstEmpty() == -1) {
                        GuiResult.Failure.ClickFailed("対象のプレイヤーのインベントリに空きがありません")
                    } else {
                        player.inventory.addItem(item.createItemStack())
                        GuiResult.Success(Unit)
                    }
                }
            }.toCommandResult()
    }

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 -> {
                val itemIds = ItemRegistry.getAllItems().map { it.id }
                context.filterStartsWith(itemIds)
            }
            2 -> listOf("1", "16", "32", "64")
            3 -> Bukkit.getOnlinePlayers().map { it.name }
            else -> emptyList()
        }
}
