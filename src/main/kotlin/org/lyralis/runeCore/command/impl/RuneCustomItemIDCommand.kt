package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.CustomGiveCommand::class)
class RuneCustomItemIDCommand : RuneCommand {
    override val name = "idc"
    override val description = "カスタムアイテムのID一覧を表示します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val itemIds = ItemRegistry.getAllItems().joinToString(", ") { it.id }
        val result =
            listOf(
                "カスタムアイテムID一覧:",
                "   $itemIds",
            ).joinToString("\n")

        return CommandResult.Success(result)
    }
}
