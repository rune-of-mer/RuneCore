package org.lyralis.runeCore.gui.builder

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.lyralis.runeCore.gui.annotation.GuiDsl
import org.lyralis.runeCore.gui.item.DecorationItem
import org.lyralis.runeCore.gui.item.GuiItem
import org.lyralis.runeCore.gui.result.GuiResult
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window

/**
 * GUI 構築用 DSL ビルダー
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
@GuiDsl
class GuiBuilder {
    var title: String = "GUI"
    var rows: Int = 3

    private val structureRows = mutableListOf<String>()
    private val ingredients = mutableMapOf<Char, GuiItem>()
    private var onCloseHandler: ((Player) -> Unit)? = null

    /**
     * GUI のレイアウト構造を定義する
     */
    fun structure(block: StructureBuilder.() -> Unit) {
        val builder = StructureBuilder().apply(block)
        structureRows.clear()
        structureRows.addAll(builder.rows)
    }

    /**
     * 装飾アイテムを配置する
     *
     * @param char レイアウト構造で使用する文字
     * @param material アイテムのマテリアル
     * @param displayName 表示名 (省略時は空白)
     */
    fun decoration(
        char: Char,
        material: Material,
        displayName: String? = null,
    ) {
        ingredients[char] = DecorationItem(material, displayName)
    }

    /**
     * クリック可能なアイテムを配置する
     *
     * @param char レイアウト構造で使用する文字
     * @param block アイテム定義ブロック
     */
    fun item(
        char: Char,
        block: ItemDefinition.() -> Unit,
    ) {
        val definition = ItemDefinition().apply(block)
        ingredients[char] = definition.build()
    }

    /**
     * GUI クローズ時のハンドラーを設定
     */
    fun onClose(handler: (Player) -> Unit) {
        onCloseHandler = handler
    }

    /**
     * InvUI の Gui を構築する
     */
    internal fun build(): Gui {
        val structureArray = structureRows.toTypedArray()

        val guiBuilder =
            Gui
                .normal()
                .setStructure(*structureArray)

        ingredients.forEach { (char, guiItem) ->
            guiBuilder.addIngredient(char, guiItem.toInvUiItem())
        }

        return guiBuilder.build()
    }

    /**
     * Window を構築してプレイヤーに表示する
     */
    internal fun buildAndShow(player: Player): GuiResult<Unit> =
        try {
            val gui = build()

            val windowBuilder =
                Window
                    .single()
                    .setViewer(player)
                    .setTitle(title)

            windowBuilder.setGui(gui)

            onCloseHandler?.let { handler ->
                windowBuilder.addCloseHandler { handler(player) }
            }

            // GUI を開く際にサウンドを再生
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)

            windowBuilder.build().open()

            GuiResult.Success(Unit)
        } catch (e: Exception) {
            GuiResult.Failure.OpenFailed(e.message ?: "Unknown error")
        }
}
