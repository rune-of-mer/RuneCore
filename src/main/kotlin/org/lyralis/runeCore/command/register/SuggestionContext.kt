package org.lyralis.runeCore.command.register

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

data class SuggestionContext(
    val source: CommandSourceStack,
    val input: String,
    val args: List<String>,
    val currentArg: String,
) {
    val sender: CommandSender get() = source.sender

    val player: Player?
        get() = sender as? Player

    // 現在の入力でフィルタリングするヘルパー
    fun filterStartsWith(candidates: List<String>): List<String> = candidates.filter { it.startsWith(currentArg, ignoreCase = true) }
}
