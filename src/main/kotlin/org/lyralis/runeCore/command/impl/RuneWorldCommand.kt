package org.lyralis.runeCore.command.impl

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.config.model.WorldConfig
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.teleport.TeleportResult
import org.lyralis.runeCore.domain.teleport.TeleportService
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * /world コマンド - ワールド間テレポートを行うコマンド
 *
 * - `/world` - ワールド選択GUIを開く
 * - `/world <ワールド名>` - 指定したワールドへテレポート（確認GUI表示）
 */
@PlayerOnlyCommand
class RuneWorldCommand(
    private val worldConfig: WorldConfig,
    private val teleportService: TeleportService,
    private val moneyService: MoneyService,
    private val crossWorldBaseCost: ULong,
) : RuneCommand {
    override val name = "world"
    override val description = "ワールド間テレポートを行います"
    override val aliases = listOf("w")

    private val config = ConfigManager.get()
    private val worldEntries = buildWorldEntries()

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow

        val worldArg = context.args.getOrNull(0)
        if (worldArg == null) {
            openWorldSelectionGui(player)
            return CommandResult.Silent
        }

        val worldEntry =
            worldEntries.find { it.id == worldArg.lowercase() }
                ?: return CommandResult.Failure.Custom("ワールド '$worldArg' は存在しません")

        if (!worldEntry.enabled) {
            return CommandResult.Failure.Custom("${worldEntry.displayName} は現在利用できません")
        }

        player.server.getWorld(worldEntry.worldName)
            ?: return CommandResult.Failure.Custom("ワールド '${worldEntry.worldName}' が見つかりません")

        val cost = calculateCost(worldEntry.crossWorldCost)
        showTeleportConfirmation(player, worldEntry, cost)

        return CommandResult.Silent
    }

    private fun openWorldSelectionGui(player: Player) {
        val balance = moneyService.getBalance(player.uniqueId)
        val currentWorldName = player.world.name

        player.openGui {
            title = "§eワールド選択"
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
                    Material.COMPASS.asGuiItem {
                        displayName = "§eワールド情報"
                        lore(
                            "",
                            "§7現在地: §f$currentWorldName",
                            "§7所持金: §6$balance Rune",
                            "",
                            "§7ワールドをクリックして",
                            "§7テレポートできます",
                        )
                    }
            }

            val slots = listOf('A', 'B', 'C', 'D', 'E', 'F')
            val availableWorlds = worldEntries.filter { it.enabled }

            availableWorlds.forEachIndexed { index, entry ->
                if (index < slots.size) {
                    val cost = calculateCost(entry.crossWorldCost)
                    val canAfford = balance >= cost
                    val isCurrentWorld = entry.worldName == currentWorldName
                    val confinedWorld = config.world

                    item(slots[index]) {
                        customItem =
                            entry.icon.asGuiItem {
                                displayName = "§e${entry.displayName}"
                                lore(
                                    "",
                                    "§7ワールド: §f${entry.worldName}",
                                    "§7料金: §6$cost Rune",
                                    "",
                                    when (entry.worldName) {
                                        confinedWorld.pvp.name ->
                                            "§dPvP あり・アイテムロストなしのバトル型コンテンツです。"
                                        confinedWorld.life.name ->
                                            "§b家を建てたり，町を作ったりできる生活型コンテンツです。"
                                        else ->
                                            "§a資源を入手できるワールドです。一定期間でリセットされます。"
                                    },
                                    "",
                                    when {
                                        isCurrentWorld -> "§7現在このワールドにいます"
                                        canAfford -> "§a左クリックでテレポート"
                                        else -> "§c残高が不足しています"
                                    },
                                )
                            }
                        onClick { action ->
                            if (isCurrentWorld) {
                                action.player.sendMessage("既にこのワールドにいます".errorMessage())
                                return@onClick GuiResult.Silent
                            }

                            showTeleportConfirmation(action.player, entry, cost)
                            GuiResult.Silent
                        }
                    }
                }
            }
        }
    }

    private fun showTeleportConfirmation(
        player: Player,
        worldEntry: WorldEntry,
        cost: ULong,
    ) {
        val balance = moneyService.getBalance(player.uniqueId)
        val canAfford = balance >= cost
        player.server.getWorld(worldEntry.worldName)

        player.openGui {
            title = "§eワールドテレポート確認"
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
                    worldEntry.icon.asGuiItem {
                        displayName = "§e${worldEntry.displayName}"
                        lore(
                            "",
                            "§7ワールド: §f${worldEntry.worldName}",
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

                    val targetWorld = action.player.server.getWorld(worldEntry.worldName)
                    if (targetWorld == null) {
                        action.player.sendMessage(
                            "ワールド '${worldEntry.worldName}' が見つかりません".errorMessage(),
                        )
                        action.player.closeInventory()
                        return@onClick GuiResult.Silent
                    }

                    val destination = targetWorld.spawnLocation

                    when (val result = teleportService.executeTeleport(action.player, destination, cost)) {
                        is TeleportResult.Success -> {
                            action.player.sendMessage(
                                "${worldEntry.displayName} へテレポートしました (料金: $cost Rune)".systemMessage(),
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

            // キャンセルボタン
            item('D') {
                customItem =
                    Material.RED_WOOL.asGuiItem {
                        displayName = "§cキャンセル"
                    }
                onClick { action ->
                    openWorldSelectionGui(action.player)
                    GuiResult.Silent
                }
            }
        }
    }

    private fun calculateCost(worldCrossWorldCost: Int): ULong = crossWorldBaseCost + worldCrossWorldCost.toULong()

    private fun buildWorldEntries(): List<WorldEntry> =
        listOf(
            WorldEntry(
                id = "life",
                displayName = "生活ワールド",
                worldName = worldConfig.life.name,
                crossWorldCost = worldConfig.life.crossWorldCost,
                icon = Material.GRASS_BLOCK,
                enabled = true,
            ),
            WorldEntry(
                id = "resource",
                displayName = "資源ワールド",
                worldName = worldConfig.resource.name,
                crossWorldCost = worldConfig.resource.crossWorldCost,
                icon = Material.DIAMOND_PICKAXE,
                enabled = true,
            ),
            WorldEntry(
                id = "resource_nether",
                displayName = "資源ネザー",
                worldName = worldConfig.resourceNether.name,
                crossWorldCost = worldConfig.resourceNether.crossWorldCost,
                icon = Material.NETHERRACK,
                enabled = true,
            ),
            WorldEntry(
                id = "resource_end",
                displayName = "資源エンド",
                worldName = worldConfig.resourceEnd.name,
                crossWorldCost = worldConfig.resourceEnd.crossWorldCost,
                icon = Material.END_STONE,
                enabled = true,
            ),
            WorldEntry(
                id = "pvp",
                displayName = "PvPワールド",
                worldName = worldConfig.pvp.name,
                crossWorldCost = worldConfig.pvp.crossWorldCost,
                icon = Material.IRON_SWORD,
                enabled = true,
            ),
        )

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 ->
                worldEntries
                    .filter { it.enabled }
                    .map { it.id }
                    .filter { it.startsWith(context.currentArg.lowercase()) }
            else -> emptyList()
        }

    private data class WorldEntry(
        val id: String,
        val displayName: String,
        val worldName: String,
        val crossWorldCost: Int,
        val icon: Material,
        val enabled: Boolean,
    )
}
