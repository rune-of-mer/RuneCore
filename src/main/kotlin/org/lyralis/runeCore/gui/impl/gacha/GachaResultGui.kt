package org.lyralis.runeCore.gui.impl.gacha

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.lyralis.runeCore.database.impl.gacha.GachaResult
import org.lyralis.runeCore.database.impl.gacha.GachaService

/**
 * ガチャ結果を表示するインベントリ名のプレフィックス
 */
const val GACHA_RESULT_INV_PREFIX = "ガチャ結果"

/**
 * ガチャ結果GUI - 抽選結果をインベントリ形式で表示
 *
 * プレイヤーはアイテムを自分のインベントリに移動して取得する
 * 閉じた時に残っているアイテムは地面にドロップされる
 */
class GachaResultGui(
    private val gachaService: GachaService,
    private val listGui: GachaListGui,
) {
    /**
     * ガチャ結果GUIを開く
     */
    fun open(
        player: Player,
        eventId: String,
        result: GachaResult,
    ) {
        val event = gachaService.getEventById(eventId)
        val eventName = event?.displayName ?: "ガチャ"

        // インベントリサイズを決定（9の倍数）
        val size =
            when {
                result.items.size <= 9 -> 9
                result.items.size <= 18 -> 18
                result.items.size <= 27 -> 27
                else -> 36
            }

        val inventory: Inventory =
            Bukkit.createInventory(
                null,
                size,
                Component.text("$GACHA_RESULT_INV_PREFIX - $eventName", NamedTextColor.GOLD),
            )

        // アイテムを配置
        result.items.forEachIndexed { index, rewardItem ->
            if (index < size) {
                inventory.setItem(index, rewardItem.toItemStack())
            }
        }

        player.openInventory(inventory)
    }

    companion object {
        /**
         * インベントリがガチャ結果GUIかどうかを判定
         */
        fun isGachaResultInventory(title: Component): Boolean {
            val plainText =
                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                    .plainText()
                    .serialize(title)
            return plainText.startsWith(GACHA_RESULT_INV_PREFIX)
        }
    }
}
