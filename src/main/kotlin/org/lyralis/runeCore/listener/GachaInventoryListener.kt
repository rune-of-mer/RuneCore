package org.lyralis.runeCore.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.gui.impl.gacha.GachaResultGui

/**
 * ガチャ結果インベントリのリスナー
 *
 * インベントリを閉じた時に残っているアイテムを地面にドロップする
 */
class GachaInventoryListener : Listener {
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val inventory = event.inventory

        // ガチャ結果インベントリかどうかをチェック
        if (!GachaResultGui.isGachaResultInventory(event.view.title())) {
            return
        }

        // 残っているアイテムを取得
        val remainingItems = inventory.contents.filterNotNull()

        if (remainingItems.isEmpty()) {
            return
        }

        // アイテムを地面にドロップ
        remainingItems.forEach { itemStack ->
            player.world.dropItemNaturally(player.location, itemStack)
        }

        player.sendMessage(
            "取り出さなかった ${remainingItems.size} 個のアイテムを足元にドロップしました".infoMessage(),
        )

        // インベントリをクリア
        inventory.clear()
    }
}
