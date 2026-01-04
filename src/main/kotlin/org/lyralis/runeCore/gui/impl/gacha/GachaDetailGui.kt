package org.lyralis.runeCore.gui.impl.gacha

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.domain.gacha.GachaService
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * ガチャ詳細GUI - 排出アイテム一覧、1回/10連ボタン、天井情報を表示
 */
class GachaDetailGui(
    private val gachaService: GachaService,
    private val listGui: GachaListGui,
) {
    /**
     * ガチャ詳細GUIを開く
     */
    fun open(
        player: Player,
        eventId: String,
    ): GuiResult<Unit> {
        val event = gachaService.getEventById(eventId) ?: return GuiResult.Failure.Custom("ガチャイベントが見つかりません")

        val ticketCount = gachaService.getPlayerTicketCount(player.inventory.contents)
        val pityCount = gachaService.getPlayerPityCount(player.uniqueId, eventId)
        val remainingToPity = event.pityThreshold - pityCount
        val canPull1 = ticketCount >= event.ticketCost.toInt()
        val canPull10 = ticketCount >= event.ticketCost.toInt() * 10

        return player.openGui {
            title = "§6${event.displayName}"
            rows = 4

            structure {
                +"# # # # I # # # #"
                +"# # L # # # 1 # #"
                +"# # # # # # X # #"
                +"B # # # # # # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)

            item('I') {
                customItem =
                    Material.ENDER_CHEST.asGuiItem {
                        displayName = "§6${event.displayName}"
                        lore(
                            "",
                            "§7必要チケット: §e${event.ticketCost} 枚/回",
                            "§7所持チケット: §a$ticketCount 枚",
                            "",
                            "§7現在のカウント: §e$pityCount/${event.pityThreshold}",
                            "§7天井まで: §a$remainingToPity 回",
                            "",
                            "§8※天井に達すると高レアリティ確定",
                        )
                    }
            }

            item('L') {
                customItem =
                    Material.BOOK.asGuiItem {
                        displayName = "§b排出アイテム一覧"
                        lore(
                            "",
                            "§7このガチャで排出される",
                            "§7アイテムを確認できます",
                            "",
                            "§aクリックで一覧を表示",
                        )
                    }
                onClick { action ->
                    GachaItemListGui(gachaService, this@GachaDetailGui).open(action.player, eventId)
                    GuiResult.Silent
                }
            }

            item('1') {
                customItem =
                    (if (canPull1) Material.GOLD_INGOT else Material.IRON_INGOT).asGuiItem {
                        displayName = if (canPull1) "§61回引く" else "§c1回引く（チケット不足）"
                        lore(
                            "",
                            "§7必要チケット: §e${event.ticketCost} 枚",
                            "",
                            if (canPull1) "§aクリックでガチャを引く" else "§cチケットが足りません",
                        )
                    }
                onClick { action ->
                    if (!canPull1) {
                        action.player.sendMessage("チケットが足りません".errorMessage())
                        return@onClick GuiResult.Silent
                    }
                    executePull(action.player, eventId, 1)
                    GuiResult.Silent
                }
            }

            item('X') {
                customItem =
                    (if (canPull10) Material.GOLD_BLOCK else Material.IRON_BLOCK).asGuiItem {
                        displayName = if (canPull10) "§610連引く" else "§c10連引く（チケット不足）"
                        lore(
                            "",
                            "§7必要チケット: §e${event.ticketCost.toInt() * 10} 枚",
                            "",
                            if (canPull10) "§aクリックでガチャを引く" else "§cチケットが足りません",
                        )
                    }
                onClick { action ->
                    if (!canPull10) {
                        action.player.sendMessage("チケットが足りません".errorMessage())
                        return@onClick GuiResult.Silent
                    }
                    executePull(action.player, eventId, 10)
                    GuiResult.Silent
                }
            }

            item('B') {
                customItem =
                    Material.ARROW.asGuiItem {
                        displayName = "§7戻る"
                    }
                onClick { action ->
                    listGui.open(action.player)
                    GuiResult.Silent
                }
            }
        }
    }

    /**
     * ガチャを実行
     */
    private fun executePull(
        player: Player,
        eventId: String,
        pullCount: Int,
    ) {
        val event = gachaService.getEventById(eventId) ?: return
        val requiredTickets = event.ticketCost.toInt() * pullCount

        if (!gachaService.consumeTickets(player.inventory.contents, requiredTickets)) {
            player.sendMessage("チケットの消費に失敗しました".errorMessage())
            return
        }

        val result = gachaService.pullGacha(player.uniqueId, eventId, pullCount)

        if (result.items.isEmpty()) {
            player.sendMessage("ガチャの抽選に失敗しました".errorMessage())
            return
        }

        if (result.isPityTriggered) {
            player.sendMessage("§6§l★天井達成！★ §e高レアリティアイテムが確定しました！".systemMessage())
        }

        val plugin = Bukkit.getPluginManager().getPlugin("RuneCore") as? JavaPlugin
        if (plugin != null) {
            Bukkit.getScheduler().runTask(
                plugin,
                Runnable {
                    GachaResultGui(gachaService, listGui).open(player, eventId, result)
                },
            )
        }
    }
}
