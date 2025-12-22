package org.lyralis.runeCore.gui.cache

import org.bukkit.inventory.ItemStack

/**
 * キャッシュされたプレイヤーの頭データ
 *
 * @property itemStack プレイヤーの頭の ItemStack
 * @property cachedAt キャッシュされた時刻（ミリ秒エポック）
 */
data class CachedPlayerHead(
    val itemStack: ItemStack,
    val cachedAt: Long = System.currentTimeMillis(),
) {
    companion object {
        const val CACHE_DURATION_MS = 30 * 60 * 1000L // 30分
    }

    /**
     * キャッシュが有効期限切れかどうかを判定
     *
     * @return 有効期限切れの場合 true
     */
    fun isExpired(): Boolean = System.currentTimeMillis() - cachedAt > CACHE_DURATION_MS
}
