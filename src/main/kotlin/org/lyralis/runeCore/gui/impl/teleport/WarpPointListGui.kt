package org.lyralis.runeCore.gui.impl.teleport

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.config.model.TeleportConfig
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.database.repository.WarpPointRepository
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.teleport.TeleportCostCalculator
import org.lyralis.runeCore.domain.teleport.TeleportResult
import org.lyralis.runeCore.domain.teleport.TeleportService
import org.lyralis.runeCore.domain.teleport.WarpPointData
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * ワープポイント一覧GUI。
 * 登録済みのワープポイントを表示し、テレポートできるようにします。
 */
class WarpPointListGui(
    private val warpPointRepository: WarpPointRepository,
    private val teleportService: TeleportService,
    private val costCalculator: TeleportCostCalculator,
    private val moneyService: MoneyService,
    private val config: TeleportConfig,
) {
    /**
     * GUIを開きます。
     *
     * @param player GUIを表示するプレイヤー
     * @return GUI表示結果
     */
    fun open(player: Player): GuiResult<Unit> {
        val warpPoints =
            when (val result = warpPointRepository.findAllByOwner(player.uniqueId)) {
                is RepositoryResult.Success -> result.data
                else -> emptyList()
            }

        val totalSlots =
            when (val result = warpPointRepository.getTotalSlots(player.uniqueId, config.defaultWarpSlots)) {
                is RepositoryResult.Success -> result.data
                else -> config.defaultWarpSlots
            }

        val balance = moneyService.getBalance(player.uniqueId)

        return player.openGui {
            title = "§eワープポイント一覧"
            rows = 4

            structure {
                +"# # # # I # # # #"
                +"# A B C D E F G #"
                +"# H J . . . . . #"
                +"# # # # # # # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)
            decoration('.', Material.GRAY_STAINED_GLASS_PANE)

            // 情報アイテム
            item('I') {
                customItem =
                    Material.BOOK.asGuiItem {
                        displayName = "§eワープポイント情報"
                        lore(
                            "",
                            "§7登録数: §f${warpPoints.size}/$totalSlots",
                            "§7所持金: §6$balance Rune",
                            "",
                            "§7ワープポイントをクリックして",
                            "§7テレポートできます",
                        )
                    }
            }

            val slots = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J')

            // ワープポイントを表示
            warpPoints.forEachIndexed { index, warpPoint ->
                if (index < slots.size) {
                    val location = warpPoint.toLocation(player.server)
                    val cost =
                        if (location != null) {
                            costCalculator.calculateCost(player.location, location)
                        } else {
                            0uL
                        }
                    val canAfford = balance >= cost

                    item(slots[index]) {
                        customItem =
                            Material.ENDER_PEARL.asGuiItem {
                                displayName = "§e${warpPoint.name}"
                                lore(
                                    "",
                                    "§7ワールド: §f${warpPoint.worldName}",
                                    "§7座標: §f${warpPoint.x.toInt()}, ${warpPoint.y.toInt()}, ${warpPoint.z.toInt()}",
                                    "§7料金: §6$cost Rune",
                                    "",
                                    if (canAfford) "§a左クリックでテレポート" else "§c残高が不足しています",
                                )
                            }
                        onClick { action ->
                            val loc = warpPoint.toLocation(action.player.server)
                            if (loc == null) {
                                action.player.sendMessage("ワールド '${warpPoint.worldName}' が見つかりません".errorMessage())
                                return@onClick GuiResult.Silent
                            }

                            // 確認GUIを表示
                            showWarpConfirmation(action.player, warpPoint, cost, loc)
                            GuiResult.Silent
                        }
                    }
                }
            }

            // 空きスロットの表示
            for (i in warpPoints.size until totalSlots.coerceAtMost(slots.size)) {
                item(slots[i]) {
                    customItem =
                        Material.LIGHT_GRAY_STAINED_GLASS_PANE.asGuiItem {
                            displayName = "§7空きスロット"
                            lore(
                                "",
                                "§7/warp create <名前>",
                                "§7で登録できます",
                            )
                        }
                }
            }
        }
    }

    private fun showWarpConfirmation(
        player: Player,
        warpPoint: WarpPointData,
        cost: ULong,
        location: Location,
    ) {
        val balance = moneyService.getBalance(player.uniqueId)
        val canAfford = balance >= cost

        player.openGui {
            title = "ワープ確認"
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
                    Material.ENDER_PEARL.asGuiItem {
                        displayName = "§e${warpPoint.name}"
                        lore(
                            "",
                            "§7ワールド: §f${warpPoint.worldName}",
                            "§7座標: §f${warpPoint.x.toInt()}, ${warpPoint.y.toInt()}, ${warpPoint.z.toInt()}",
                            "",
                            "§7料金: §6$cost Rune",
                            "§7所持金: §6$balance Rune",
                            "",
                            if (canAfford) "§a料金を支払えます" else "§c料金が不足しています",
                        )
                    }
            }

            // テレポートボタン
            item('C') {
                customItem =
                    Material.LIME_WOOL.asGuiItem {
                        displayName = if (canAfford) "§aテレポート" else "§cテレポート (残高不足)"
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

                    when (val result = teleportService.executeTeleport(action.player, location, cost)) {
                        is TeleportResult.Success -> {
                            action.player.sendMessage(
                                "${warpPoint.name} へテレポートしました (料金: $cost Rune)".systemMessage(),
                            )
                        }
                        is TeleportResult.InsufficientBalance -> {
                            action.player.sendMessage(
                                "所持金が不足しています (必要: $cost Rune, 所持: ${result.current} Rune)".errorMessage(),
                            )
                        }
                        else -> {
                            action.player.sendMessage("テレポートに失敗しました".errorMessage())
                        }
                    }

                    action.player.closeInventory()
                    GuiResult.Silent
                }
            }

            // 戻るボタン
            item('D') {
                customItem =
                    Material.ARROW.asGuiItem {
                        displayName = "§7戻る"
                    }
                onClick { action ->
                    open(action.player)
                    GuiResult.Silent
                }
            }
        }
    }
}
