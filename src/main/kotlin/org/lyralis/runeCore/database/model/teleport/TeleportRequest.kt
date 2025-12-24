package org.lyralis.runeCore.database.model.teleport

import org.bukkit.Location
import java.time.Instant
import java.util.UUID

/**
 * プレイヤー間テレポートリクエストのモデル。
 * メモリ内で管理され、タイムアウト後は自動的に削除されます。
 */
data class TeleportRequest(
    val requesterId: UUID,
    val requesterName: String,
    val targetId: UUID,
    val requesterLocation: Location,
    val cost: ULong,
    val createdAt: Instant = Instant.now(),
) {
    /**
     * リクエストが期限切れかどうかを判定します。
     *
     * @param timeoutSeconds タイムアウト時間（秒）
     * @return 期限切れの場合はtrue
     */
    fun isExpired(timeoutSeconds: Long = 60): Boolean = Instant.now().isAfter(createdAt.plusSeconds(timeoutSeconds))

    /**
     * リクエストの残り有効時間（秒）を取得します。
     *
     * @param timeoutSeconds タイムアウト時間（秒）
     * @return 残り時間（秒）。期限切れの場合は0
     */
    fun remainingSeconds(timeoutSeconds: Long = 60): Long {
        val expiresAt = createdAt.plusSeconds(timeoutSeconds)
        val remaining = expiresAt.epochSecond - Instant.now().epochSecond
        return remaining.coerceAtLeast(0)
    }
}
