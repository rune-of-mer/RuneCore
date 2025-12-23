package org.lyralis.runeCore.command.impl

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext

const val TRASH_INV_NAME = "ゴミ箱 - 取り扱い注意"

@PlayerOnlyCommand
class RuneTrashCommand : RuneCommand {
    override val name = "trash"
    override val description = "ゴミ箱を開きます"
    override val aliases = listOf("gomi")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val trashInventory =
            player.server.createInventory(
                null,
                54,
                Component.text(TRASH_INV_NAME, NamedTextColor.RED),
            )
        player.openInventory(trashInventory)

        return CommandResult.Success("ゴミ箱を開きました．閉じると確認ダイアログが表示されます")
    }
}
