package org.lyralis.runeCore.command.impl.experience

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.domain.experience.ExperienceService
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.ExperienceAddCommand::class)
class RuneExperienceAddCommand(
    private val experienceService: ExperienceService,
) : RuneCommand {
    override val name = "add"
    override val description = "指定したプレイヤーに経験値を付与します"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val experience =
            context.arg(1, "500").toULongOrNull()
                ?: return CommandResult.Failure.InvalidArgument("経験値は数値で指定してください")

        if (experience !in 1uL..<1000000uL) {
            return CommandResult.Failure.InvalidArgument("付与できる経験値は1以上1000000未満です")
        }

        val target = context.arg(2, player.name)
        val targetPlayer =
            Bukkit.getServer().getPlayer(target)
                ?: return CommandResult.Failure.ExecutionFailed("指定したプレイヤーは存在しません")

        val newExperience =
            experienceService.grantExperience(targetPlayer, experience)
                ?: return CommandResult.Failure.ExecutionFailed("経験値の付与に失敗しました")

        return CommandResult.Success("$target に $experience EXP を付与しました (現在の経験値: $newExperience)")
    }
}
