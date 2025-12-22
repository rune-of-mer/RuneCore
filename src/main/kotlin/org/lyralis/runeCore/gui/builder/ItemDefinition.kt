package org.lyralis.runeCore.gui.builder

import org.bukkit.Material
import org.lyralis.runeCore.gui.annotation.GuiDsl
import org.lyralis.runeCore.gui.handler.ClickAction
import org.lyralis.runeCore.gui.item.ClickableItem
import org.lyralis.runeCore.gui.item.GuiItem
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * アイテム定義用 DSL クラス
 *
 * ```kotlin
 * item('A') {
 *     material = Material.DIAMOND_SWORD
 *     displayName = "戦闘設定"
 *     lore = listOf("クリックして開く")
 *
 *     onClick { action ->
 *         action.player.sendMessage("クリック！")
 *         GuiResult.Success(Unit)
 *     }
 * }
 * ```
 */
@GuiDsl
class ItemDefinition {
    var material: Material = Material.STONE
    var displayName: String = ""
    var lore: List<String> = emptyList()
    var amount: Int = 1

    private var clickHandler: ((ClickAction) -> GuiResult<Unit>)? = null
    private var leftClickHandler: ((ClickAction) -> GuiResult<Unit>)? = null
    private var rightClickHandler: ((ClickAction) -> GuiResult<Unit>)? = null

    /**
     * クリック時の処理を定義
     */
    fun onClick(handler: (ClickAction) -> GuiResult<Unit>) {
        clickHandler = handler
    }

    /**
     * 左クリック時の処理を定義
     */
    fun onLeftClick(handler: (ClickAction) -> GuiResult<Unit>) {
        leftClickHandler = handler
    }

    /**
     * 右クリック時の処理を定義
     */
    fun onRightClick(handler: (ClickAction) -> GuiResult<Unit>) {
        rightClickHandler = handler
    }

    internal fun build(): GuiItem =
        ClickableItem(
            material = material,
            displayName = displayName,
            loreLines = lore,
            amount = amount,
            clickHandler = { action ->
                when {
                    action.isLeftClick && leftClickHandler != null -> leftClickHandler!!(action)
                    action.isRightClick && rightClickHandler != null -> rightClickHandler!!(action)
                    clickHandler != null -> clickHandler!!(action)
                    else -> GuiResult.Silent
                }
            },
        )
}
