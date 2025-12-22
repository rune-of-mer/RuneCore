package org.lyralis.runeCore.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.item.CustomItemType
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.item.model.CustomItemAction

/**
 * カスタムアイテムの使用イベントを処理するリスナー
 */
class CustomItemInteractListener : Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return

        val customItem = ItemRegistry.getFromItemStack(item) ?: return

        // Usable でない場合は処理しない
        if (customItem !is CustomItemType.Usable) return

        val action =
            when (event.action) {
                Action.RIGHT_CLICK_BLOCK -> {
                    if (!customItem.isUsableOnRightClick) return
                    CustomItemAction.RIGHT_CLICK
                }
                Action.LEFT_CLICK_BLOCK -> {
                    if (!customItem.isUsableOnLeftClick) return
                    CustomItemAction.LEFT_CLICK
                }
                else -> return
            }

        customItem.requiredPermission?.let { permission ->
            if (!permission.has(player)) {
                player.sendMessage("このアイテムを使用する権限がありません".errorMessage())
                event.isCancelled = true
                return
            }
        }

        val success = customItem.onUse(player, action)
        if (success) {
            event.isCancelled = true
            if (customItem is CustomItemType.Consumable) {
                val newAmount = item.amount - customItem.consumeAmount
                if (newAmount <= 0) {
                    item.amount = 0
                } else {
                    item.amount = newAmount
                }
            }
        }
    }
}
