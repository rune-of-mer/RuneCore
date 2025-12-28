package org.lyralis.runeCore.command.impl.experience

import org.bukkit.Bukkit
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.permission.Permission

@PlayerOnlyCommand
@CommandPermission(Permission.Admin.ExperienceCommand::class)
class RuneExperienceCommand(
    experienceService: ExperienceService,
) : RuneCommand {
    override val name = "experience"
    override val description = "経験値を管理します"
    override val aliases = listOf("exp")

    override val subcommands: List<RuneCommand> =
        listOf(
            RuneExperienceAddCommand(experienceService),
        )

    override fun execute(context: RuneCommandContext): CommandResult = CommandResult.Failure.InvalidArgument("/exp add <経験値> [プレイヤー]")

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 -> context.filterStartsWith(listOf("add"))
            2 -> {
                return listOf("100", "200", "300", "400", "500")
            }
            3 -> {
                return Bukkit.getOnlinePlayers().map { it.name }
            }
            else -> emptyList()
        }
}
