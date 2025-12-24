package org.lyralis.runeCore.command.impl.level

import org.bukkit.Material
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.database.impl.experience.ExperienceCalculator
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.gui.toCommandResult

/**
 * /level コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能．
 */
@PlayerOnlyCommand
class RuneLevelCommand(
    moneyService: MoneyService,
    val experienceService: ExperienceService,
) : RuneCommand {
    override val name = "level"
    override val description = "現在のレベルを確認します"
    override val aliases = listOf("lv")

    override val subcommands =
        listOf(
            RuneLevelConvertCommand(moneyService, experienceService),
        )

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val currentLevel = experienceService.getLevel(player.uniqueId)
        val currentExp = experienceService.getExperience(player.uniqueId)
        val nextExp = ExperienceCalculator.getExperienceForNextLevel(currentLevel)
        val maxLevel = ExperienceCalculator.getMaxLevel()

        val result =
            if (currentLevel >= maxLevel) {
                listOf(
                    "   レベル: $currentLevel/$maxLevel",
                    "   経験値: $currentExp",
                    "",
                    "すでに最大レベルに到達しています",
                    "超過分は上限突破後に引き続きカウントされます",
                )
            } else {
                listOf(
                    "   レベル: $currentLevel/$maxLevel",
                    "   経験値: $currentExp/$nextExp",
                )
            }

        return player
            .openGui {
                title = "レベル/経験値"
                rows = 3

                structure {
                    +"# # # # # # # # #"
                    +"# # H # # # L # #"
                    +"# # # # # # # # #"
                }

                decoration('#', Material.BLACK_STAINED_GLASS_PANE)

                item('L') {
                    material = Material.EXPERIENCE_BOTTLE
                    displayName = "お金からレベルアップ"
                    lore =
                        listOf(
                            "お金を経験値に変換してレベルアップできます",
                            "このボタンからでは 1000 Rune を経験値に変換します",
                            "さらに変換するには \"/level convert <変換金額>\" を実行します",
                            "",
                            "一度に変換できる経験値は10万Expまでです",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        player.performCommand("level convert 1000")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                item('H') {
                    customItem =
                        player.getCachedPlayerHead {
                            displayName = "${player.name}のレベル情報:"
                            lore(result)
                        }
                    onClick {
                        GuiResult.Silent
                    }
                }
            }.toCommandResult()
    }
}
