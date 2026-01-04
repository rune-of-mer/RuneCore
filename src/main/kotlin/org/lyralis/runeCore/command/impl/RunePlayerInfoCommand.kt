package org.lyralis.runeCore.command.impl

import org.bukkit.Material
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.domain.experience.ExperienceCalculator
import org.lyralis.runeCore.domain.player.PlayerService
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.gui.toCommandResult
import org.lyralis.runeCore.utils.getFormattedPlayTime

private data class InfoItemData(
    val displayName: String,
    val material: Material,
    val lore: List<String>,
    val command: String?,
)

@PlayerOnlyCommand
class RunePlayerInfoCommand(
    private val playerService: PlayerService,
) : RuneCommand {
    override val name = "playerinfo"
    override val description = "プレイヤー情報を開きます"
    override val aliases = listOf("pinfo")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val playerData = playerService.getPlayerData(player.uniqueId)
        val playerStats = playerService.getStats(player.uniqueId)

        if (playerData == null || playerStats == null) {
            return CommandResult.Failure.ExecutionFailed("プレイヤーデータの取得に失敗しました")
        }

        // プレイヤーデータが存在しているので，明示的にnull許容を外す
        val kd = playerService.getKillDeathRatio(player.uniqueId)!!
        val playTime = player.getFormattedPlayTime()

        val maxLevel = ExperienceCalculator.getMaxLevel()
        val multiplier = ExperienceCalculator.getMultiplier()

        return player
            .openGui {
                title = "プレイヤー情報"
                rows = 3

                structure {
                    +"# # # # H # # # #"
                    +"# L E M P B T . #"
                    +"# # # # # # # # #"
                }

                decoration('#', Material.BLACK_STAINED_GLASS_PANE)
                decoration('.', Material.WHITE_STAINED_GLASS_PANE)

                val infoContents =
                    mapOf(
                        'L' to
                            InfoItemData(
                                displayName = "レベル",
                                material = Material.EXPERIENCE_BOTTLE,
                                lore =
                                    listOf(
                                        "",
                                        "§7現在のレベル: §eLv${playerData.level}/Lv$maxLevel",
                                        "",
                                        "§7最大レベルに到達している場合、その超過分は",
                                        "§7レベル上限が解放されたときに使用されます",
                                        "",
                                        "§aクリックで詳細を確認",
                                    ),
                                command = "level",
                            ),
                        'E' to
                            InfoItemData(
                                displayName = "経験値",
                                material = Material.KNOWLEDGE_BOOK,
                                lore =
                                    listOf(
                                        "",
                                        "§7総経験値: §6${playerData.experience} §7Exp",
                                        "",
                                        "§7現在適用されている経験値の乗数: §6$multiplier §7Exp",
                                        "",
                                        "§aクリックで詳細を確認",
                                    ),
                                command = "level",
                            ),
                        'M' to
                            InfoItemData(
                                displayName = "ルーン",
                                material = Material.EMERALD,
                                lore =
                                    listOf(
                                        "",
                                        "§7現在の所持金: §6${playerData.balance} §7Rune",
                                    ),
                                command = null,
                            ),
                        'P' to
                            InfoItemData(
                                displayName = "PvPvE",
                                material = Material.DIAMOND_SWORD,
                                lore =
                                    listOf(
                                        "",
                                        "§7キル数: §6${playerStats.kills}",
                                        "§7デス数: §6${playerStats.deaths}",
                                        "   §7K/D: §6$kd",
                                        "§7モブキル数: §6${playerStats.mobKills}",
                                    ),
                                command = null,
                            ),
                        'B' to
                            InfoItemData(
                                displayName = "ブロック",
                                material = Material.OAK_WOOD,
                                lore =
                                    listOf(
                                        "",
                                        "§7ブロック破壊数: §6${playerStats.blocksDestroys}",
                                        "§7ブロック設置数: §6${playerStats.blocksPlaces}",
                                        "",
                                        "§7集計対象のブロックは建築ブロックのみで、",
                                        "§7花などの装飾ブロックは集計されません。",
                                    ),
                                command = null,
                            ),
                        'T' to
                            InfoItemData(
                                displayName = "セッション",
                                material = Material.CLOCK,
                                lore =
                                    listOf(
                                        "",
                                        "§7累計プレイ時間: §6$playTime",
                                        "§7累計ログイン日数: §6${playerStats.loginDays}",
                                        "",
                                    ),
                                command = "playtime",
                            ),
                    )

                infoContents.forEach { (char, data) ->
                    item(char) {
                        displayName = data.displayName
                        material = data.material
                        lore = data.lore
                        onClick { action ->
                            if (data.command == null) {
                                return@onClick GuiResult.Silent
                            }

                            if (!action.isLeftClick) {
                                return@onClick GuiResult.Silent
                            }

                            action.player.closeInventory()
                            player.performCommand(data.command)
                            return@onClick GuiResult.Success(Unit)
                        }
                    }
                }

                item('H') {
                    customItem =
                        player.getCachedPlayerHead {
                            displayName = player.name
                            lore {
                                +""
                                +"§7あなたです!"
                            }
                        }
                    onClick {
                        GuiResult.Silent
                    }
                }
            }.toCommandResult()
    }
}
