package org.lyralis.runeCore.gui.impl.teleport

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.model.teleport.TeleportRequest
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.teleport.TeleportRequestManager
import org.lyralis.runeCore.teleport.TeleportResult
import org.lyralis.runeCore.teleport.TeleportService

/**
 * テレポートリクエスト確認GUI。
 * リクエストを承認または却下するためのGUIを表示します。
 */
class TeleportConfirmGui(
    private val requestManager: TeleportRequestManager,
    private val teleportService: TeleportService,
    private val moneyService: MoneyService,
    private val request: TeleportRequest,
) {
    /**
     * GUIを開きます。
     *
     * @param player GUIを表示するプレイヤー（リクエストの受信者）
     * @return GUI表示結果
     */
    fun open(player: Player): GuiResult<Unit> {
        val balance = moneyService.getBalance(player.uniqueId)
        val canAfford = balance >= request.cost

        return player.openGui {
            title = "テレポート確認"
            rows = 3

            structure {
                +"# # # # # # # # #"
                +"# # C # I # D # #"
                +"# # # # # # # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)

            // 情報表示
            item('I') {
                customItem =
                    Material.PAPER.asGuiItem {
                        displayName = "§eテレポート情報"
                        lore(
                            "",
                            "§7送信者: §f${request.requesterName}",
                            "§7料金: §6${request.cost} Rune",
                            "",
                        )
                    }
            }

            // 承認ボタン
            item('C') {
                customItem =
                    Material.LIME_WOOL.asGuiItem {
                        displayName = if (canAfford) "§a承認" else "§c承認 (残高不足)"
                        if (!canAfford) {
                            lore("", "§c所持金が不足しています")
                        }
                    }
                onClick { action ->
                    if (!canAfford) {
                        action.player.sendMessage("所持金が不足しています".errorMessage())
                        action.player.closeInventory()
                        return@onClick GuiResult.Silent
                    }

                    val requester = action.player.server.getPlayer(request.requesterId)
                    if (requester == null) {
                        action.player.sendMessage("リクエスト送信者がオフラインです".errorMessage())
                        action.player.closeInventory()
                        requestManager.removeRequest(action.player.uniqueId, request.requesterId)
                        return@onClick GuiResult.Silent
                    }

                    // リクエスト送信者を承認者の位置へテレポート
                    when (
                        val result =
                            teleportService.executeTeleport(
                                requester,
                                action.player.location,
                                request.cost,
                            )
                    ) {
                        is TeleportResult.Success -> {
                            requester.sendMessage(
                                "${action.player.name} へテレポートしました (料金: ${request.cost} Rune)".systemMessage(),
                            )
                            action.player.sendMessage("${requester.name} があなたの元へテレポートしました".infoMessage())
                            requestManager.removeRequest(action.player.uniqueId, request.requesterId)
                        }
                        is TeleportResult.InsufficientBalance -> {
                            requester.sendMessage("所持金が不足しているためテレポートできませんでした".errorMessage())
                            action.player.sendMessage("${requester.name} の所持金が不足しています".errorMessage())
                        }
                        is TeleportResult.PlayerInDZ -> {
                            requester.sendMessage("あなたはダークゾーン(DZ)にいるため、テレポートリクエストがかき消されました。テレポートできません".errorMessage())
                            action.player.sendMessage("${requester.name} はダークゾーン(DZ)にいるため、テレポートはキャンセルされました".errorMessage())
                            requestManager.removeRequest(action.player.uniqueId, request.requesterId)
                        }
                        else -> {
                            action.player.sendMessage("テレポート処理に失敗しました".errorMessage())
                        }
                    }

                    action.player.closeInventory()
                    GuiResult.Silent
                }
            }

            // 却下ボタン
            item('D') {
                customItem =
                    Material.RED_WOOL.asGuiItem {
                        displayName = "§c却下"
                    }
                onClick { action ->
                    val requester = action.player.server.getPlayer(request.requesterId)
                    requester?.sendMessage("${action.player.name} がテレポートリクエストを却下しました".errorMessage())
                    requestManager.removeRequest(action.player.uniqueId, request.requesterId)
                    action.player.sendMessage("テレポートリクエストを却下しました".infoMessage())
                    action.player.closeInventory()
                    GuiResult.Silent
                }
            }
        }
    }
}
