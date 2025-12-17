package org.lyralis.runeCore.component.action

import org.bukkit.entity.Player
import org.lyralis.runeCore.component.errorMessage

/**
 * データベースの処理エラーなどプレイが続行できなかった場合に適切なメッセージを使い，セッションを切断するアクションのクラスです．
 *
 * @param player ターゲットとなるプレイヤー
 */
class PlayerSessionAction(
    val player: Player,
) {
    private val result = listOf("予期せぬエラーが発生しました。公式Discordにて運営のサポートを受けてください。", "----", "${player.name}(${player.uniqueId})")

    /**
     * プレイヤーのセッションを切断します．
     */
    fun setErrorKick() {
        player.kick(result.errorMessage())
    }
}
