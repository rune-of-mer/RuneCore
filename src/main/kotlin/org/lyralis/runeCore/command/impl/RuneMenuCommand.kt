package org.lyralis.runeCore.command.impl

import net.kyori.adventure.sound.Sound
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

                /**
                 *  B - メニューブックをインベントリに追加
                 *  H - プレイヤー情報
                 *  P - プレイヤー一覧ページを開く
                 *  T - プレイ時間を表示
                 *  S - 設定ページを開く
                 *  L - レペル情報ページを開く
                 *  Q - ログアウトページを開く
                 *  G - ゴミ箱を開く
                 *  O - ショップを開く
                 */
                structure {
                    +"T # # # H # # # P"
                    +"# L O # # # # # #"
                    +"S # # # Q # # # G"
                }

                decoration('#', Material.WHITE_STAINED_GLASS_PANE)

                // メニューアイテム定義
                val menuContents =
                    mapOf(
                        'P' to
                            MenuItemData(
                                displayName = "プレイヤー一覧",
                                material = Material.PLAYER_HEAD,
                                lore =
                                    listOf(
                                        "プレイヤー一覧を開きます",
                                        "現在 $onlinePlayers/$maxPlayers 人がプレイ中です",
                                    ),
                                command = "playerlist",
                            ),
                        'L' to
                            MenuItemData(
                                displayName = "レベル情報",
                                material = Material.KNOWLEDGE_BOOK,
                                lore =
                                    listOf(
                                        "レベル情報ページを開きます",
                                    ),
                                command = "level",
                            ),
                        'O' to
                            MenuItemData(
                                displayName = "ショップ",
                                material = Material.EMERALD_BLOCK,
                                lore = listOf("左クリックでショップを開きます"),
                                command = "shop",
                            ),
                        'S' to
                            MenuItemData(
                                displayName = "設定",
                                material = Material.COMPASS,
                                lore =
                                    listOf(
                                        "設定を開きます",
                                        "ボスバーの表示などを切り替えることができます",
                                    ),
                                command = "settings",
                            ),
                        'G' to
                            MenuItemData(
                                displayName = "ゴミ箱",
                                material = Material.POISONOUS_POTATO,
                                lore =
                                    listOf(
                                        "ゴミ箱を開きます",
                                        "不要なアイテムを売却・処分することができます。",
                                        "アイテムはルーンと交換されますが、一度捨てたアイテムは帰ってきません",
                                    ),
                                command = "trash",
                            ),
                        'Q' to
                            MenuItemData(
                                displayName = "ログアウト",
                                material = Material.DARK_OAK_DOOR,
                                lore =
                                    listOf(
                                        "サーバーからログアウトします",
                                        "飛行中や一部コンテンツをプレイしている間はログアウトできません",
                                    ),
                                command = "logout",
                            ),
                        'T' to
                            MenuItemData(
                                displayName = "累計プレイ時間",
                                material = Material.CLOCK,
                                lore = listOf("現在の累計プレイ時間を表示します"),
                                command = "playtime",
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
                                +"レベル: Lv$level (最大Lv${ExperienceCalculator.getMaxLevel()})"
                                +"総経験値: $experience Exp"
                                +"所持金: $money Rune"
                                +"----"
                                +"UUID: ${player.uniqueId}"
                                +"Ping値: ${player.ping}ms"
                                +"現在地: ${player.world.name}"
                                +""
                                +"左クリックでプレイヤー情報を開きます"
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
