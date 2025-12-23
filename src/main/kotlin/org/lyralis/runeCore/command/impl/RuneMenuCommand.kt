package org.lyralis.runeCore.command.impl

import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.database.impl.experience.ExperienceCalculator
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.gui.toCommandResult
import org.lyralis.runeCore.item.ItemRegistry

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

                // プレイヤー情報
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

                // プレイヤー一覧ページ
                item('P') {
                    displayName = "プレイヤー一覧"
                    material = Material.PLAYER_HEAD
                    lore =
                        listOf(
                            "プレイヤー一覧を開きます",
                            "現在 $onlinePlayers/$maxPlayers 人がプレイ中です",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("playerlist")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                // レベル情報ページ
                item('L') {
                    displayName = "レベル情報"
                    material = Material.KNOWLEDGE_BOOK
                    lore =
                        listOf(
                            "レベル情報ページを開きます",
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

                // ショップ
                item('O') {
                    displayName = "ショップ"
                    material = Material.EMERALD_BLOCK
                    lore =
                        listOf(
                            "右クリックでショップを開きます",
                            "左クリックでオークションを開きます",
                        )
                    onClick { action ->
                        when {
                            action.isRightClick -> {
                                action.player.closeInventory()
                                // ショップ
                                return@onClick GuiResult.Success(Unit)
                            }
                            action.isLeftClick -> {
                                action.player.closeInventory()
                                // オークション
                                return@onClick GuiResult.Success(Unit)
                            }
                            else -> {
                                return@onClick GuiResult.Silent
                            }
                        }
                    }
                }

                // 設定ページ
                item('S') {
                    displayName = "設定"
                    material = Material.COMPASS
                    lore =
                        listOf(
                            "設定を開きます",
                            "ボスバーの表示などを切り替えることができます",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("settings")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                // 設定ページ
                item('G') {
                    displayName = "ゴミ箱"
                    material = Material.POISONOUS_POTATO
                    lore =
                        listOf(
                            "ゴミ箱を開きます",
                            "不要なアイテムを捨てることができます",
                            "アイテムは 1Rune と交換されますが、一度捨てたアイテムは帰ってきません",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("trash")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                // ログアウトページ
                item('Q') {
                    displayName = "ログアウト"
                    material = Material.DARK_OAK_DOOR
                    lore =
                        listOf(
                            "サーバーからログアウトします",
                            "飛行中や一部コンテンツをプレイしている間はログアウトできません",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("logout")
                        return@onClick GuiResult.Success(Unit)
                    }
                }

                // プレイ時間を表示
                item('T') {
                    displayName = "累計プレイ時間"
                    material = Material.CLOCK
                    lore =
                        listOf(
                            "現在の累計プレイ時間を表示します",
                        )
                    onClick { action ->
                        if (!action.isLeftClick) {
                            return@onClick GuiResult.Silent
                        }

                        action.player.closeInventory()
                        player.performCommand("playtime")
                        return@onClick GuiResult.Success(Unit)
                    }
                }
            }.toCommandResult()
    }
}
