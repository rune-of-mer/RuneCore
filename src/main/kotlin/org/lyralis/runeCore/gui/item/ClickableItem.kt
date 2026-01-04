package org.lyralis.runeCore.gui.item

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.gui.handler.ClickAction
import org.lyralis.runeCore.gui.result.GuiResult
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.impl.AbstractItem

/**
 * クリック可能なアイテム
 *
 * @param material アイテムのマテリアル
 * @param displayName 表示名
 * @param loreLines 説明文
 * @param amount アイテム数
 * @param customItem カスタムアイテム（設定時は material/displayName/lore より優先）
 * @param clickHandler クリック時のハンドラー
 */
class ClickableItem(
    private val material: Material,
    private val displayName: String,
    private val loreLines: List<String> = emptyList(),
    private val amount: Int = 1,
    private val customItem: ItemStack? = null,
    private val clickHandler: ((ClickAction) -> GuiResult<Unit>)? = null,
) : GuiItem {
    override fun toInvUiItem(): Item = ClickableInvUiItem()

    private inner class ClickableInvUiItem : AbstractItem() {
        override fun getItemProvider(): ItemProvider =
            ItemProvider {
                customItem?.clone() ?: org.bukkit.inventory.ItemStack(material, amount).apply {
                    editMeta { meta ->
                        meta.displayName(Component.text(displayName))
                        if (loreLines.isNotEmpty()) {
                            meta.lore(loreLines.map { Component.text(it) })
                        }
                    }
                }
            }

        override fun handleClick(
            clickType: ClickType,
            player: Player,
            event: InventoryClickEvent,
        ) {
            val handler = clickHandler ?: return

            val action =
                ClickAction(
                    player = player,
                    clickType = clickType,
                    slot = event.slot,
                    currentItem = event.currentItem,
                    cursorItem = event.cursor,
                )

            handler(action)
        }
    }
}
