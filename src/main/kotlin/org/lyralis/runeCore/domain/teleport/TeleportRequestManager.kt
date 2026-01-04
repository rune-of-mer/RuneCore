package org.lyralis.runeCore.domain.teleport

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import org.lyralis.runeCore.domain.teleport.TeleportRequest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * プレイヤー間テレポートリクエストを管理するクラス。
 * リクエストはメモリ内で管理され、タイムアウト後は自動的に削除されます。
 */
class TeleportRequestManager(
    private val plugin: JavaPlugin,
    private val timeoutSeconds: Long = 60,
) {
    // 受信者UUID -> リクエストリスト のマップ
    private val pendingRequests = ConcurrentHashMap<UUID, MutableList<TeleportRequest>>()
    private var cleanupTask: BukkitTask? = null

    /**
     * リクエスト管理を開始します。
     * 定期的に期限切れリクエストをクリーンアップするタスクを起動します。
     */
    fun start() {
        // 5秒（100tick）ごとに期限切れリクエストをクリーンアップ
        cleanupTask =
            plugin.server.scheduler.runTaskTimer(
                plugin,
                Runnable {
                    cleanupExpiredRequests()
                },
                100L,
                100L,
            )
    }

    /**
     * リクエスト管理を停止します。
     */
    fun stop() {
        cleanupTask?.cancel()
        pendingRequests.clear()
    }

    /**
     * テレポートリクエストを追加します。
     * 同一送信者からの既存リクエストは上書きされます。
     *
     * @param request 追加するリクエスト
     * @return 追加に成功した場合はtrue
     */
    fun addRequest(request: TeleportRequest): Boolean {
        val list = pendingRequests.getOrPut(request.targetId) { mutableListOf() }
        // 同一送信者からの既存リクエストを削除
        list.removeIf { it.requesterId == request.requesterId }
        list.add(request)
        return true
    }

    /**
     * 指定したプレイヤーへの有効なリクエストリストを取得します。
     *
     * @param targetId 受信者のUUID
     * @return 有効なリクエストのリスト
     */
    fun getRequestsFor(targetId: UUID): List<TeleportRequest> =
        pendingRequests[targetId]
            ?.filter { !it.isExpired(timeoutSeconds) }
            ?.toList()
            ?: emptyList()

    /**
     * 特定のリクエストを削除して取得します。
     *
     * @param targetId 受信者のUUID
     * @param requesterId 送信者のUUID
     * @return 削除されたリクエスト、存在しない場合はnull
     */
    fun removeRequest(
        targetId: UUID,
        requesterId: UUID,
    ): TeleportRequest? {
        val list = pendingRequests[targetId] ?: return null
        val request = list.find { it.requesterId == requesterId }
        list.removeIf { it.requesterId == requesterId }
        if (list.isEmpty()) {
            pendingRequests.remove(targetId)
        }
        return request
    }

    /**
     * 指定したプレイヤーへの全リクエストを削除します。
     *
     * @param targetId 受信者のUUID
     */
    fun clearRequestsFor(targetId: UUID) {
        pendingRequests.remove(targetId)
    }

    /**
     * 指定したプレイヤーに保留中のリクエストがあるかどうかを確認します。
     *
     * @param targetId 受信者のUUID
     * @return リクエストがある場合はtrue
     */
    fun hasRequestsFor(targetId: UUID): Boolean = getRequestsFor(targetId).isNotEmpty()

    /**
     * 期限切れリクエストをクリーンアップします。
     */
    private fun cleanupExpiredRequests() {
        pendingRequests.forEach { (_, list) ->
            list.removeIf { it.isExpired(timeoutSeconds) }
        }
        pendingRequests.entries.removeIf { it.value.isEmpty() }
    }
}
