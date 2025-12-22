package org.lyralis.runeCore.gui.builder

import org.lyralis.runeCore.gui.annotation.GuiDsl

/**
 * GUI のレイアウト構造を定義するビルダー
 *
 * ```kotlin
 * structure {
 *     +"# # # # # # # # #"
 *     +"# A . B . C . D #"
 *     +"# # # # # # # # #"
 * }
 * ```
 */
@GuiDsl
class StructureBuilder {
    internal val rows = mutableListOf<String>()

    /**
     * 行を追加する (unary plus オペレーター)
     *
     * @receiver 行のレイアウト文字列 (スペース区切りで各スロットの文字を指定)
     */
    operator fun String.unaryPlus() {
        rows.add(this)
    }
}
