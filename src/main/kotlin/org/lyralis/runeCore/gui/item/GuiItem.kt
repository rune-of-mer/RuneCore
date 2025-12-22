package org.lyralis.runeCore.gui.item

import xyz.xenondevs.invui.item.Item

/**
 * GUI アイテムの基底 sealed interface
 *
 * すべての GUI アイテムはこのインターフェースを実装する
 */
sealed interface GuiItem {
    /**
     * InvUI の Item に変換する
     */
    fun toInvUiItem(): Item
}
