package dev.m1sk9.runeCore.command.register

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

data class RuneCommandContext(
    val source: CommandSourceStack,
    val args: Array<String>,
) {
    val sender: CommandSender get() = source.sender

    val player: Player? get() = sender as? Player

    val playerOrThrow: Player
        get() = player ?: throw IllegalStateException("Player only command")

    fun arg(index: Int, def: String): String = args.getOrNull(index) ?: def

    fun argOrThrow(index: Int): String = args.getOrNull(index) ?: throw IllegalArgumentException("Argument at index $index not found")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RuneCommandContext
        return source == other.source && args.contentEquals(other.args)
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}
