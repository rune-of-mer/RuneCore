package org.lyralis.runeCore.command.impl

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.CustomGiveCommand::class)
class RuneCustomGiveCommand : RuneCommand {
    override val name = "givec"
    override val description = "カスタムアイテムをインベントリに追加します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val itemId =
            context.args.getOrNull(0)
                ?: return CommandResult.Failure.InvalidArgument("/giveu <ID> [個数] [プレイヤー]")

        val customItem =
            ItemRegistry.getById(itemId)
                ?: return CommandResult.Failure.ExecutionFailed("アイテム $itemId は存在しません")

        val amount = context.args.getOrNull(1)?.toIntOrNull() ?: 1
        if (amount !in 1..64) {
            return CommandResult.Failure.InvalidArgument("個数は1〜64の範囲で指定してください")
        }

        val targetName = context.arg(2, player.name)
        val target =
            Bukkit.getPlayer(targetName)
                ?: return CommandResult.Failure.ExecutionFailed("指定したプレイヤーは存在しません")

        if (target.inventory.firstEmpty() == -1) {
            return CommandResult.Failure.ExecutionFailed("対象のプレイヤーのインベントリに空きがありません")
        }

        val itemStack = customItem.createItemStack(amount)
        target.inventory.addItem(itemStack)

        return if (target == player) {
            CommandResult.Success("${customItem.displayName} を付与しました")
        } else {
            CommandResult.Success("${target.name} に ${customItem.displayName} を付与しました")
        }
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
