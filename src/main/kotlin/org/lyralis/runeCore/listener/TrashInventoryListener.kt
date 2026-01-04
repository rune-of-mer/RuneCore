package org.lyralis.runeCore.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.lyralis.runeCore.command.impl.TRASH_INV_NAME
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.gui.result.ConfirmationResult
import org.lyralis.runeCore.gui.template.showConfirmation
import org.lyralis.runeCore.item.ItemRegistry

class TrashInventoryListener(
    private val plugin: Plugin,
    private val moneyService: MoneyService,
) : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val inventory = event.inventory

        if (event.view.title() != Component.text(TRASH_INV_NAME, NamedTextColor.RED)) {
            return
        }

        val trashContents = inventory.contents.filterNotNull()
        if (trashContents.isEmpty()) return

        /**
         * NOTE:
         * InventoryCloseEvent の内部で GUI を開いてしまうとイベントが再発火してしまい，無限ループになり StackOverflowError が Throw されてしまう
         * インベントリをクリアして、確認ダイアログ閉じ時の再処理を防ぐ
         */
        inventory.clear()

        val savedItems = trashContents.map { it.clone() }
        val (customItems, vanillaItems) =
            trashContents.partition {
                ItemRegistry.isCustomItem(it)
            }

        var trashCost = 0uL
        var isRare = false

        vanillaItems.forEach { item ->
            val itemMeta = item.itemMeta
            if (!isRare && itemMeta.hasRarity()) {
                val itemLarity = itemMeta.rarity
                if (itemLarity == ItemRarity.RARE || itemLarity == ItemRarity.EPIC) {
                    isRare = true
                }
            }

            trashCost += item.amount.toULong()
        }

        customItems.forEach { itemStack ->
            val customItem = ItemRegistry.getFromItemStack(itemStack)!!
            val rarityWeight = customItem.rarity.weight.toULong()
            trashCost += rarityWeight * itemStack.amount.toULong()

            if (rarityWeight < 20u) {
                isRare = true
            }
        }

        // NOTE: InventoryCloseEvent 内で GUI を開くと問題が発生するため、次のティックで実行 (これがいいのかは正直わからない)
        Bukkit.getScheduler().runTask(
            plugin,
            Runnable {
                player.showConfirmation {
                    title = "売却・処分確認"
                    message = "${trashContents.size} アイテムを $trashCost Rune で売却・処分します．よろしいですか?"
                    confirmText = "売却する${
                        if (isRare) {
                            " - 警告: 高価なアイテムが売却されようとしています"
                        } else {
                            ""
                        }
                    }"
                    confirmMaterial = Material.EMERALD_BLOCK
                    denyText = "キャンセル"
                    denyMaterial = Material.REDSTONE_BLOCK

                    onResult { result ->
                        when (result) {
                            ConfirmationResult.Confirmed -> {
                                moneyService.addBalance(player, trashCost)
                                player.sendMessage("${trashContents.size} アイテムを $trashCost Rune で売却・処分しました".infoMessage())
                            }
                            ConfirmationResult.Denied -> {
                                returnItemsToPlayer(player, savedItems)
                                player.sendMessage("売却をキャンセルしました。アイテムは全てインベントリへ返却しました".systemMessage())
                            }
                            ConfirmationResult.Cancelled -> {
                            }
                        }
                    }
                }
            },
        )
    }

    /**
     * アイテムをプレイヤーに返却する
     * インベントリに入らない場合は足元にドロップ
     */
    private fun returnItemsToPlayer(
        player: Player,
        items: List<ItemStack>,
    ) {
        items.forEach { item ->
            val leftover = player.inventory.addItem(item)
            leftover.values.forEach { excess ->
                player.world.dropItemNaturally(player.location, excess)
            }
        }
    }
}
