package dev.m1sk9.runeCore.command.register

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

data class RuneCommandContext(
    val source: CommandSourceStack,
    val args: Map<String, Any>,
) {
    val sender: CommandSender get() = source.sender

    val player: Player?
        get() = sender as? Player

    val playerOrThrow: Player
        get() = player ?: throw IllegalStateException("Player only command")

    inline fun <reified T> getArgument(name: String): T? = args[name] as? T
}
