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
                    "",
                    "§7   レベル: §e$currentLevel §7/ §e$maxLevel",
                    "§7   経験値: §6$currentExp",
                    "",
                    "§6すでに最大レベルに到達しています",
                    "§7超過分は上限突破後に引き続きカウントされます",
                )
            } else {
                listOf(
                    "",
                    "§7   レベル: §e$currentLevel §7/ §e$maxLevel",
                    "§7   経験値: §6$currentExp §7/ §6$nextExp",
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
                            "",
                            "§7お金を経験値に変換してレベルアップできます",
                            "",
                            "§eこのボタンからでは §61000 Rune §eを経験値に変換します",
                            "§7さらに変換するには §b/level convert <変換金額> §7を実行します",
                            "",
                            "§c一度に変換できる経験値は10万Expまでです",
                            "",
                            "§aクリックで変換",
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
