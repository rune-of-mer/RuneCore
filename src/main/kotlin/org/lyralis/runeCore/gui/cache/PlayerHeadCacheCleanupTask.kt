package org.lyralis.runeCore.gui.cache

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.logging.Logger

class PlayerHeadCacheCleanupTask(
    private val plugin: Plugin,
    private val logger: Logger,
) {
    private var task: BukkitTask? = null

    companion object {
        private const val CLEANUP_INTERVAL_TICKS = 20L * 60 * 5 // 5分 (20 tick = 1秒)
        private const val INITIAL_DELAY_TICKS = 20L * 60 // 1分後に開始
    }

    /**
     * クリーンアップタスクを開始
     */
    fun start() {
        if (task != null) {
            logger.warning("PlayerHeadCacheCleanupTask is already running")
            return
        }

        task =
            plugin.server.scheduler.runTaskTimer(
                plugin,
                Runnable {
                    val removedCount = PlayerHeadCacheManager.cleanupExpiredCache()
                    if (removedCount > 0) {
                        logger.info("Cleaned up $removedCount expired player head cache entries")
                    }
                },
                INITIAL_DELAY_TICKS,
                CLEANUP_INTERVAL_TICKS,
            )

        logger.info("PlayerHeadCacheCleanupTask started (interval: 5 minutes)")
    }

    /**
     * クリーンアップタスクを停止
     */
    fun stop() {
        task?.cancel()
        task = null
        logger.info("PlayerHeadCacheCleanupTask stopped")
    }

    /**
     * タスクが実行中かどうか
     */
    fun isRunning(): Boolean = task != null
}
