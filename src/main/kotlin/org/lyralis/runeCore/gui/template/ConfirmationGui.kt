package org.lyralis.runeCore.gui.template

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.gui.annotation.GuiDsl
import org.lyralis.runeCore.gui.builder.GuiBuilder
import org.lyralis.runeCore.gui.result.ConfirmationResult
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * 確認ダイアログ用テンプレートビルダー
 *
 * ```kotlin
 * player.showConfirmation {
 *     title = "削除確認"
 *     message = "本当に削除しますか？"
 *     confirmText = "削除する"
 *     denyText = "キャンセル"
 *
 *     onResult { result ->
 *         when (result) {
 *             ConfirmationResult.Confirmed -> deleteItem()
 *             ConfirmationResult.Denied -> cancel()
 *             ConfirmationResult.Cancelled -> {}
 *         }
 *     }
 * }
 * ```
 */
@GuiDsl
class ConfirmationGuiBuilder {
    var title: String = "確認"
    var message: String = "この操作を実行しますか?"
    var confirmText: String = "確認"
    var denyText: String = "キャンセル"
    var confirmMaterial: Material = Material.LIME_WOOL
    var denyMaterial: Material = Material.RED_WOOL

    private var onResultHandler: ((ConfirmationResult) -> Unit)? = null

    /**
     * 結果のハンドラーを設定
     */
    fun onResult(handler: (ConfirmationResult) -> Unit) {
        onResultHandler = handler
    }

    /**
     * ダイアログを構築してプレイヤーに表示
     */
    internal fun buildAndShow(player: Player): GuiResult<Unit> {
        // クロージャでキャプチャできるように配列を使用
        val resultHolder = arrayOf<ConfirmationResult>(ConfirmationResult.Cancelled)

        val guiBuilder =
            GuiBuilder().apply {
                this.title = this@ConfirmationGuiBuilder.title
                this.rows = 3

                structure {
                    +"# # # # # # # # #"
                    +"# # # # M # # # #"
                    +"# # C # # # D # #"
                }

                decoration('#', Material.BLACK_STAINED_GLASS_PANE)

                decoration('M', Material.PAPER, this@ConfirmationGuiBuilder.message)

                item('C') {
                    material = this@ConfirmationGuiBuilder.confirmMaterial
                    displayName = this@ConfirmationGuiBuilder.confirmText
                    amount = 1

                    onClick {
                        resultHolder[0] = ConfirmationResult.Confirmed
                        it.player.closeInventory()
                        GuiResult.Success(Unit)
                    }
                }

                item('D') {
                    material = this@ConfirmationGuiBuilder.denyMaterial
                    displayName = this@ConfirmationGuiBuilder.denyText
                    amount = 1

                    onClick {
                        resultHolder[0] = ConfirmationResult.Denied
                        it.player.closeInventory()
                        GuiResult.Success(Unit)
                    }
                }

                onClose {
                    this@ConfirmationGuiBuilder.onResultHandler?.invoke(resultHolder[0])
                }
            }

        return guiBuilder.buildAndShow(player)
    }
}

/**
 * 確認ダイアログを表示する拡張関数
 *
 * ```kotlin
 * player.showConfirmation {
 *     title = "削除確認"
 *     message = "本当に削除しますか？"
 *
 *     onResult { result ->
 *         when (result) {
 *             ConfirmationResult.Confirmed -> deleteItem()
 *             ConfirmationResult.Denied -> {}
 *             ConfirmationResult.Cancelled -> {}
 *         }
 *     }
 * }
 * ```
 */
fun Player.showConfirmation(block: ConfirmationGuiBuilder.() -> Unit): GuiResult<Unit> {
    val builder = ConfirmationGuiBuilder().apply(block)
    return builder.buildAndShow(this)
}
