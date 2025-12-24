package org.lyralis.runeCore.gui.impl.teleport

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.model.teleport.TeleportRequest
import org.lyralis.runeCore.gui.getCachedPlayerHead
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.teleport.TeleportRequestManager
import org.lyralis.runeCore.teleport.TeleportService

/**
 * 複数のテレポートリクエストから選択するGUI。
 */
class TeleportRequestSelectGui(
    private val requestManager: TeleportRequestManager,
    private val teleportService: TeleportService,
    private val moneyService: MoneyService,
    private val requests: List<TeleportRequest>,
) {
    /**
     * GUIを開きます。
     *
     * @param player GUIを表示するプレイヤー
     * @return GUI表示結果
     */
    fun open(player: Player): GuiResult<Unit> {
        val rows = calculateRows(requests.size)

        return player.openGui {
            title = "テレポートリクエスト選択"
            this.rows = rows

            structure {
                when (rows) {
                    2 -> {
                        +"# # # # # # # # #"
                        +"# A B C D E F G #"
                    }
                    3 -> {
                        +"# # # # # # # # #"
                        +"# A B C D E F G #"
                        +"# # # # # # # # #"
                    }
                    4 -> {
                        +"# # # # # # # # #"
                        +"# A B C D E F G #"
                        +"# H I J K L M N #"
                        +"# # # # # # # # #"
                    }
                    else -> {
                        +"# # # # # # # # #"
                        +"# A B C D E F G #"
                        +"# H I J K L M N #"
                        +"# O P Q R S T U #"
                        +"# # # # # # # # #"
                    }
                }
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)

            val slots = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U')

            requests.forEachIndexed { index, request ->
                if (index < slots.size) {
                    item(slots[index]) {
                        customItem =
                            request.requesterId.getCachedPlayerHead {
                                displayName = "§e${request.requesterName}"
                                lore(
                                    "",
                                    "§7料金: §6${request.cost} Rune",
                                    "§7残り時間: §f${request.remainingSeconds()}秒",
                                    "",
                                    "§aクリックで確認画面を開く",
                                )
                            }
                        onClick { action ->
                            TeleportConfirmGui(requestManager, teleportService, moneyService, request)
                                .open(action.player)
                            GuiResult.Silent
                        }
                    }
                }
            }
        }
    }

    private fun calculateRows(count: Int): Int =
        when {
            count <= 7 -> 3
            count <= 14 -> 4
            else -> 5
        }
}
