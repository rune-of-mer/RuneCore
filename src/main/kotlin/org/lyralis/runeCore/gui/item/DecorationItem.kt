package org.lyralis.runeCore.gui.item

import net.kyori.adventure.text.Component
import org.bukkit.Material
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.impl.SimpleItem

/**
 * クリック不可の装飾アイテム
 *
 * @param material アイテムのマテリアル
 * @param displayName 表示名 (null の場合は空白)
 */
class DecorationItem(
    private val material: Material,
    private val displayName: String? = null,
) : GuiItem {
    override fun toInvUiItem(): Item =
        SimpleItem(
            ItemProvider {
                org.bukkit.inventory.ItemStack(material).apply {
                    editMeta { meta ->
                        meta.displayName(
                            displayName?.let { Component.text(it) } ?: Component.empty(),
                        )
                    }
                }
            },
        )
}
