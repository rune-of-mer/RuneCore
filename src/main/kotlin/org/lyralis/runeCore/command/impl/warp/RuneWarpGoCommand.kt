package org.lyralis.runeCore.command.impl.warp

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.systemMessage
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

@PlayerOnlyCommand
class RuneWarpGoCommand(
    private val warpPointRepository: WarpPointRepository,
    private val teleportService: TeleportService,
    private val costCalculator: TeleportCostCalculator,
    private val moneyService: MoneyService,
) : RuneCommand {
    override val name = "go"
    override val description = "ワープポイントへテレポートします"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val warpName =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.InvalidArgument("/warp go <拠点名>")

        val warpPoint =
            when (val result = warpPointRepository.findByOwnerAndName(player.uniqueId, warpName)) {
                is RepositoryResult.Success -> result.data
                is RepositoryResult.NotFound -> {
                    return CommandResult.Failure.Custom("ワープポイント '$warpName' が見つかりません")
                }
                else -> return CommandResult.Failure.ExecutionFailed("ワープポイントの取得に失敗しました")
            }

        val location =
            warpPoint.toLocation(player.server)
                ?: return CommandResult.Failure.Custom("ワールド '${warpPoint.worldName}' が見つかりません")

        val cost = costCalculator.calculateCost(player.location, location)
        showWarpConfirmation(player, warpPoint, cost)

        return CommandResult.Silent
    }

    private fun showWarpConfirmation(
        player: Player,
        warpPoint: WarpPointData,
        cost: ULong,
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

                    val location = warpPoint.toLocation(action.player.server)
                    if (location == null) {
                        action.player.sendMessage("ワールド '${warpPoint.worldName}' が見つかりません".errorMessage())
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

            item('D') {
                customItem =
                    Material.RED_WOOL.asGuiItem {
                        displayName = "§cキャンセル"
                    }
                onClick { action ->
                    action.player.closeInventory()
                    GuiResult.Silent
                }
            }
        }
    }

    override fun suggest(context: SuggestionContext): List<String> {
        val player = context.player ?: return emptyList()
        return when (context.args.size) {
            1 -> {
                when (val result = warpPointRepository.findAllByOwner(player.uniqueId)) {
                    is RepositoryResult.Success ->
                        result.data
                            .map { it.name }
                            .filter { it.lowercase().startsWith(context.currentArg.lowercase()) }
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
    }
}
