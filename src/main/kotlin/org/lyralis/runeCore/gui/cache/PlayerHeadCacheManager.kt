package org.lyralis.runeCore.gui.cache

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerHeadCacheManager {
    private val headCache = ConcurrentHashMap<UUID, CachedPlayerHead>()

    /**
     * プレイヤーの頭を取得（キャッシュまたは新規作成）
     *
     * @param playerId プレイヤーの UUID
     * @return プレイヤーの頭の ItemStack
     */
    fun getOrCreatePlayerHead(playerId: UUID): ItemStack {
        // キャッシュから取得を試みる
        val cached = headCache[playerId]
        if (cached != null && !cached.isExpired()) {
            return cached.itemStack.clone() // ItemStack は可変なので clone を返す
        }

        // キャッシュミスまたは期限切れの場合は新規作成
        val newHead = createPlayerHead(playerId)
        headCache[playerId] = CachedPlayerHead(newHead.clone())
        return newHead
    }

    /**
     * プレイヤーの頭を取得（OfflinePlayer 版）
     *
     * @param player オフラインプレイヤー
     * @return プレイヤーの頭の ItemStack
     */
    fun getOrCreatePlayerHead(player: OfflinePlayer): ItemStack = getOrCreatePlayerHead(player.uniqueId)

    /**
     * 特定プレイヤーのキャッシュを削除
     *
     * @param playerId プレイヤーの UUID
     */
    fun invalidateCache(playerId: UUID) {
        headCache.remove(playerId)
    }

    /**
     * 期限切れキャッシュをクリーンアップ
     *
     * @return 削除されたキャッシュ数
     */
    fun cleanupExpiredCache(): Int {
        val expiredKeys =
            headCache.entries
                .filter { it.value.isExpired() }
                .map { it.key }

        expiredKeys.forEach { headCache.remove(it) }
        return expiredKeys.size
    }

    /**
     * 全てのキャッシュをクリア
     */
    fun clearAllCache() {
        headCache.clear()
    }

    /**
     * 現在のキャッシュサイズを取得
     *
     * @return キャッシュ数
     */
    fun getCacheSize(): Int = headCache.size

    /**
     * プレイヤーの頭 ItemStack を生成
     *
     * @param playerId プレイヤーの UUID
     * @return 生成された ItemStack
     */
    private fun createPlayerHead(playerId: UUID): ItemStack {
        val offlinePlayer = Bukkit.getOfflinePlayer(playerId)
        return ItemStack(Material.PLAYER_HEAD).apply {
            editMeta { meta ->
                if (meta is SkullMeta) {
                    meta.owningPlayer = offlinePlayer
                }
            }
        }
    }
}
