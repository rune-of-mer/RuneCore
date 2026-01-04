package org.lyralis.runeCore.command.impl

import org.bukkit.Material
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.domain.experience.ExperienceCalculator
import org.lyralis.runeCore.domain.experience.ExperienceService
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.gui.toCommandResult

@PlayerOnlyCommand
class RunePlayerInfoCommand(
    private val experienceService: ExperienceService,
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "playerinfo"
    override val description = "プレイヤー情報を開きます"
    override val aliases = listOf("pinfo")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val level = experienceService.getLevel(player.uniqueId)
        val experience = experienceService.getExperience(player.uniqueId)
        val money = moneyService.getBalance(player.uniqueId)

        val maxLevel = ExperienceCalculator.getMaxLevel()
        val multiplier = ExperienceCalculator.getMultiplier()
        val requiredExperience = ExperienceCalculator.getExperienceForLevel(level)

        return player
            .openGui {
                title = "プレイヤー情報"
                rows = 3

                structure {
                    +"# # # # H # # # #"
                    +"# # L # E # M # #"
                    +"# # # # # # # # #"
                }

                decoration('#', Material.WHITE_STAINED_GLASS_PANE)

                item('H') {
                    customItem =
                        player.getCachedPlayerHead {
                            displayName = player.name
                            lore {
                                +"あなたです!"
                            }
                        }
                    onClick {
                        GuiResult.Silent
                    }
                }

                item('L') {
                    displayName = "レベル"
                    material = Material.EXPERIENCE_BOTTLE
                    lore =
                        listOf(
                            "現在のレベル: Lv$level",
                            "   レベル上限: Lv$maxLevel",
                            "",
                            "最大レベルに到達している場合、その超過分はレベル上限が解放されたときに使用されます",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("level")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                item('E') {
                    displayName = "経験値"
                    material = Material.KNOWLEDGE_BOOK
                    lore =
                        listOf(
                            "次のレベルまで: $requiredExperience Exp",
                            "総経験値: $experience Exp",
                            "",
                            "現在適用されている経験値の乗数: $multiplier Exp",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("level")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                item('M') {
                    displayName = "ルーン"
                    material = Material.EMERALD
                    lore =
                        listOf(
                            "現在の所持金: $money Rune",
                        )
                    onClick {
                        GuiResult.Silent
                    }
                }
            }.toCommandResult()
    }
}
