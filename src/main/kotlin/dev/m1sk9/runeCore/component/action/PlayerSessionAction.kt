package dev.m1sk9.runeCore.component.action

import dev.m1sk9.runeCore.component.errorMessage
import org.bukkit.entity.Player

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
