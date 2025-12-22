package org.lyralis.runeCore.gui.handler

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * クリックアクションのコンテキスト情報
 *
 * @param player クリックしたプレイヤー
 * @param clickType クリックの種類
 * @param slot クリックされたスロット番号
 * @param currentItem クリックされたスロットのアイテム
 * @param cursorItem カーソルにあるアイテム
 */
data class ClickAction(
    val player: Player,
    val clickType: ClickType,
    val slot: Int,
    val currentItem: ItemStack?,
    val cursorItem: ItemStack?,
) {
    val isLeftClick: Boolean get() = clickType.isLeftClick

    val isRightClick: Boolean get() = clickType.isRightClick

    val isShiftClick: Boolean get() = clickType.isShiftClick

    val isMiddleClick: Boolean get() = clickType == ClickType.MIDDLE

    val isDoubleClick: Boolean get() = clickType == ClickType.DOUBLE_CLICK

    val isKeyboardClick: Boolean get() = clickType.isKeyboardClick
}
