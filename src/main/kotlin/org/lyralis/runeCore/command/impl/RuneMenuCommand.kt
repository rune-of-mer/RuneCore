package org.lyralis.runeCore.command.impl

import net.kyori.adventure.sound.Sound
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

private data class MenuItemData(
    val displayName: String,
    val material: Material,
    val lore: List<String>,
    val command: String,
)

@PlayerOnlyCommand
class RuneMenuCommand(
    private val experienceService: ExperienceService,
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "menu"
    override val description = "メニューを開きます"
    override val aliases = listOf("m", "me", "meru")

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val onlinePlayers =
            player.server.onlinePlayers.size
        val maxPlayers = player.server.maxPlayers
        val level = experienceService.getLevel(player.uniqueId)
        val experience = experienceService.getExperience(player.uniqueId)
        val money = moneyService.getBalance(player.uniqueId)

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE,
                Sound.Source.PLAYER,
                1.0f,
                1.0f,
            ),
        )
        return player
            .openGui {
                title = "メニュー"
                rows = 3

                structure {
                    +"T # # # H # # # P"
                    +"# L O A # # W # #"
                    +"S # # # Q # # # G"
                }

                decoration('#', Material.WHITE_STAINED_GLASS_PANE)

                val menuContents =
                    mapOf(
                        'P' to
                            MenuItemData(
                                displayName = "プレイヤー一覧",
                                material = Material.PLAYER_HEAD,
                                lore =
                                    listOf(
                                        "",
                                        "§7プレイヤー一覧を開きます",
                                        "§7現在 §e$onlinePlayers§7/§e$maxPlayers §7人がプレイ中です",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "playerlist",
                            ),
                        'L' to
                            MenuItemData(
                                displayName = "レベル情報",
                                material = Material.KNOWLEDGE_BOOK,
                                lore =
                                    listOf(
                                        "",
                                        "§7レベル情報ページを開きます",
                                        "§7経験値の確認やレベルアップができます",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "level",
                            ),
                        'O' to
                            MenuItemData(
                                displayName = "ショップ",
                                material = Material.EMERALD_BLOCK,
                                lore =
                                    listOf(
                                        "",
                                        "§7様々なアイテムを購入できます",
                                        "§7お金を使って便利なアイテムを手に入れましょう",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "shop",
                            ),
                        'S' to
                            MenuItemData(
                                displayName = "設定",
                                material = Material.COMPASS,
                                lore =
                                    listOf(
                                        "",
                                        "§7設定を開きます",
                                        "§7ボスバーの表示などを切り替えることができます",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "settings",
                            ),
                        'G' to
                            MenuItemData(
                                displayName = "ゴミ箱",
                                material = Material.POISONOUS_POTATO,
                                lore =
                                    listOf(
                                        "",
                                        "§7ゴミ箱を開きます",
                                        "§7不要なアイテムを売却・処分することができます",
                                        "",
                                        "§cアイテムはルーンと交換されます",
                                        "§c一度捨てたアイテムは帰ってきません",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "trash",
                            ),
                        'Q' to
                            MenuItemData(
                                displayName = "ログアウト",
                                material = Material.DARK_OAK_DOOR,
                                lore =
                                    listOf(
                                        "",
                                        "§7サーバーからログアウトします",
                                        "",
                                        "§c飛行中や一部コンテンツをプレイしている間は",
                                        "§cログアウトできません",
                                        "",
                                        "§aクリックでログアウト",
                                    ),
                                command = "logout",
                            ),
                        'T' to
                            MenuItemData(
                                displayName = "累計プレイ時間",
                                material = Material.CLOCK,
                                lore =
                                    listOf(
                                        "",
                                        "§7現在の累計プレイ時間を表示します",
                                        "",
                                        "§aクリックで確認",
                                    ),
                                command = "playtime",
                            ),
                        'W' to
                            MenuItemData(
                                displayName = "ワールド移動",
                                material = Material.GRASS_BLOCK,
                                lore =
                                    listOf(
                                        "",
                                        "§7ワールド間テレポートを行います",
                                        "§7生活ワールド、資源ワールドなどに移動できます",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "world",
                            ),
                        'A' to
                            MenuItemData(
                                displayName = "ガチャ",
                                material = Material.ENDER_CHEST,
                                lore =
                                    listOf(
                                        "",
                                        "§7ガチャを開きます",
                                        "§7チケットを使用してアイテムを入手できます",
                                        "",
                                        "§aクリックで開く",
                                    ),
                                command = "gacha",
                            ),
                    )

                menuContents.forEach { (char, data) ->
                    item(char) {
                        displayName = data.displayName
                        material = data.material
                        lore = data.lore
                        onClick { action ->
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
                                +"§7レベル: §e$level §7/ §e${ExperienceCalculator.getMaxLevel()}"
                                +"§7総経験値: §6$experience §7Exp"
                                +"§7所持金: §6$money §7Rune"
                                +""
                                +"§8UUID: §7${player.uniqueId}"
                                +"§8Ping値: §7${player.ping}ms"
                                +"§8現在地: §7${player.world.name}"
                                +""
                                +"§aクリックでプレイヤー情報を開く"
                            }
                        }
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }
                        action.player.closeInventory()
                        player.performCommand("playerinfo")
                        return@onClick GuiResult.Success(Unit)
                    }
                }
            }.toCommandResult()
    }
}
