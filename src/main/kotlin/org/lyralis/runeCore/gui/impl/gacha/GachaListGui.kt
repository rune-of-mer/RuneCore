package org.lyralis.runeCore.gui.impl.gacha

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.domain.gacha.GachaService
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * ガチャ一覧GUI - アクティブなガチャイベントを表示
 */
class GachaListGui(
    private val gachaService: GachaService,
) {
    /**
     * ガチャ一覧GUIを開く
     */
    fun open(player: Player): GuiResult<Unit> {
        val activeEvents = gachaService.getActiveEvents()
        val ticketCount = gachaService.getPlayerTicketCount(player.inventory.contents)

        return player.openGui {
            title = "ガチャ"
            rows = 4

            structure {
                +"# # # # I # # # #"
                +"# A B C D E F # #"
                +"# # # # # # # # #"
                +"# # # # # # # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)

            item('I') {
                customItem =
                    Material.PAPER.asGuiItem {
                        displayName = "§eガチャチケット"
                        lore(
                            "",
                            "§7所持数: §a$ticketCount 枚",
                            "",
                            "§7ガチャを引くにはチケットが必要です",
                        )
                    }
            }

            val slots = listOf('A', 'B', 'C', 'D', 'E', 'F')

            activeEvents.forEachIndexed { index, event ->
                if (index < slots.size) {
                    val pityCount = gachaService.getPlayerPityCount(player.uniqueId, event.id)
                    val remainingToPity = event.pityThreshold - pityCount

                    item(slots[index]) {
                        customItem =
                            Material.ENDER_CHEST.asGuiItem {
                                displayName = "§6${event.displayName}"
                                lore(
                                    "",
                                    "§7必要チケット: §e${event.ticketCost} 枚/回",
                                    "§7天井まで: §a$remainingToPity 回",
                                    "",
                                    "§aクリックで詳細を開く",
                                )
                            }
                        onClick { action ->
                            GachaDetailGui(gachaService, this@GachaListGui).open(action.player, event.id)
                            GuiResult.Silent
                        }
                    }
                }
            }

            if (activeEvents.isEmpty()) {
                item('A') {
                    customItem =
                        Material.BARRIER.asGuiItem {
                            displayName = "§c現在開催中のガチャはありません"
                            lore(
                                "",
                                "§7後ほどお試しください",
                            )
                        }
                }
            }
        }
    }
}
