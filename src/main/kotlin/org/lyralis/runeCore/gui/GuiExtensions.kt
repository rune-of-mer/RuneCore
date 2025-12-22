package org.lyralis.runeCore.gui

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.gui.annotation.GuiDsl
import org.lyralis.runeCore.gui.builder.GuiBuilder
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * GUI を構築してプレイヤーに表示する
 *
 * ```kotlin
 * player.openGui {
 *     title = "設定メニュー"
 *     rows = 3
 *
 *     structure {
 *         +"# # # # # # # # #"
 *         +"# A . B . C . D #"
 *         +"# # # # # # # # #"
 *     }
 *
 *     decoration('#', Material.BLACK_STAINED_GLASS_PANE)
 *
 *     item('A') {
 *         material = Material.DIAMOND_SWORD
 *         displayName = "戦闘設定"
 *         onClick { action ->
 *             GuiResult.Success(Unit)
 *         }
 *     }
 * }
 * ```
 */
fun Player.openGui(block: GuiBuilder.() -> Unit): GuiResult<Unit> {
    val builder = GuiBuilder().apply(block)
    return builder.buildAndShow(this)
}

/**
 * Material から ItemStack を構築する DSL
 *
 * ```kotlin
 * val item = Material.DIAMOND_SWORD.asGuiItem {
 *     displayName = "強力な剣"
 *     lore {
 *         +"攻撃力: +10"
 *         +"耐久値: 100"
 *     }
 * }
 * ```
 */
fun Material.asGuiItem(block: GuiItemBuilder.() -> Unit = {}): ItemStack {
    val builder = GuiItemBuilder(this).apply(block)
    return builder.build()
}

/**
 * GUI アイテム構築用クラス
 */
@GuiDsl
class GuiItemBuilder(
    private val material: Material,
) {
    var displayName: String = ""
    var amount: Int = 1
    var glowing: Boolean = false
    private val loreLines = mutableListOf<String>()

    /**
     * 説明文を設定する（可変長引数）
     */
    fun lore(vararg lines: String) {
        loreLines.addAll(lines)
    }

    /**
     * 説明文を設定する（DSL ブロック）
     */
    fun lore(block: LoreBuilder.() -> Unit) {
        val builder = LoreBuilder().apply(block)
        loreLines.addAll(builder.lines)
    }

    internal fun build(): ItemStack =
        ItemStack(material, amount).apply {
            editMeta { meta ->
                if (displayName.isNotEmpty()) {
                    meta.displayName(Component.text(displayName))
                }
                if (loreLines.isNotEmpty()) {
                    meta.lore(loreLines.map { Component.text(it) })
                }
                if (glowing) {
                    meta.setEnchantmentGlintOverride(true)
                }
            }
        }
}

/**
 * 説明文構築用 DSL クラス
 */
@GuiDsl
class LoreBuilder {
    internal val lines = mutableListOf<String>()

    /**
     * 行を追加する（unary plus オペレーター）
     */
    operator fun String.unaryPlus() {
        lines.add(this)
    }
}
