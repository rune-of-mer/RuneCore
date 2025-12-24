package org.lyralis.runeCore.gui.impl.shop

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * ショップ検索の入力待機状態を管理するシングルトン
 */
object ShopSearchManager {
    private val awaitingSearch = ConcurrentHashMap<UUID, Long>()

    /**
     * プレイヤーを検索入力待機状態にする
     *
     * @param player 対象のプレイヤー
     */
    fun startAwaiting(player: Player) {
        awaitingSearch[player.uniqueId] = System.currentTimeMillis()
    }

    /**
     * プレイヤーが検索入力待機中かどうかを確認する
     *
     * @param player 対象のプレイヤー
     * @return 待機中の場合は true
     */
    fun isAwaiting(player: Player): Boolean {
        awaitingSearch[player.uniqueId] ?: return false
        return true
    }

    /**
     * 検索入力待機を終了する
     *
     * @param player 対象のプレイヤー
     */
    fun stopAwaiting(player: Player) {
        awaitingSearch.remove(player.uniqueId)
    }

    /**
     * プレイヤー退出時のクリーンアップ
     *
     * @param uuid プレイヤーの UUID
     */
    fun cleanup(uuid: UUID) {
        awaitingSearch.remove(uuid)
    }
}
