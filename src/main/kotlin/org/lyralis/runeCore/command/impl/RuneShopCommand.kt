package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.gui.impl.shop.ShopMainGui
import org.lyralis.runeCore.gui.toCommandResult

@PlayerOnlyCommand
class RuneShopCommand(
    private val shopMainGui: ShopMainGui,
) : RuneCommand {
    override val name = "shop"
    override val description = "ショップを開きます"
    override val aliases = listOf("store", "market")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        return shopMainGui.open(player).toCommandResult()
    }
}
