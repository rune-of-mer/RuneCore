package org.lyralis.runeCore.component.actionbar

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 一時通知のデータを保持するクラス．
 */
private data class NotificationItem(
    val message: Component,
    val startTime: Long,
)

/**
 * ActionBar の表示を管理するマネージャー．
 *
 * 一時的な通知と常時表示を優先度付きで管理します．
 * 複数の通知がキューに追加された場合、順番に表示されます．
 */
object ActionBarManager {
    private lateinit var plugin: Plugin

    private val persistentProviders = ConcurrentHashMap<UUID, PersistentActionBarProvider>()
    private val notificationQueues = ConcurrentHashMap<UUID, ConcurrentLinkedQueue<NotificationItem>>()
    private val currentNotifications = ConcurrentHashMap<UUID, NotificationItem>()
    private var updateTask: BukkitTask? = null

    private const val UPDATE_INTERVAL_TICKS = 20L
    private const val TEMPORARY_NOTIFICATION_DURATION_MS = 2000L

    /**
     * プラグインインスタンスを設定して ActionBar 更新タスクを開始します．
     *
     * @param pluginInstance プラグインインスタンス
     */
    fun initialize(pluginInstance: Plugin) {
        plugin = pluginInstance
        startUpdateTask()
    }

    /**
     * プレイヤーの常時表示を登録します．
     *
     * @param player プレイヤー
     * @param provider 常時表示のコンテンツを提供するプロバイダー
     */
    fun registerPersistentProvider(
        player: Player,
        provider: PersistentActionBarProvider,
    ) {
        persistentProviders[player.uniqueId] = provider
        player.sendActionBar(provider.getContent(player))
    }

    /**
     * プレイヤーの常時表示を解除します．
     *
     * @param player プレイヤー
     */
    fun unregisterPersistentProvider(player: Player) {
        persistentProviders.remove(player.uniqueId)
        notificationQueues.remove(player.uniqueId)
        currentNotifications.remove(player.uniqueId)
    }

    /**
     * 一時的な通知を表示します．
     *
     * 通知が終わるまで常時表示は一時停止されます．
     * 複数の通知が同時に追加された場合、キューに追加され順番に表示されます．
     *
     * @param player プレイヤー
     * @param message 表示するメッセージ
     */
    fun showTemporaryNotification(
        player: Player,
        message: Component,
    ) {
        val uuid = player.uniqueId
        val queue = notificationQueues.computeIfAbsent(uuid) { ConcurrentLinkedQueue() }

        // 現在表示中の通知がない場合は即座に表示
        if (currentNotifications[uuid] == null) {
            val notification = NotificationItem(message, System.currentTimeMillis())
            currentNotifications[uuid] = notification
            player.sendActionBar(message)
        } else {
            // 既に表示中の通知がある場合はキューに追加
            queue.offer(NotificationItem(message, 0L))
        }
    }

    /**
     * 全ての常時表示を停止し、更新タスクをキャンセルします．
     */
    fun shutdown() {
        updateTask?.cancel()
        updateTask = null
        persistentProviders.clear()
        notificationQueues.clear()
        currentNotifications.clear()
    }

    private fun startUpdateTask() {
        updateTask =
            Bukkit.getScheduler().runTaskTimer(
                plugin,
                Runnable {
                    val currentTime = System.currentTimeMillis()

                    for ((uuid, provider) in persistentProviders) {
                        val player = Bukkit.getPlayer(uuid) ?: continue

                        val currentNotification = currentNotifications[uuid]
                        if (currentNotification != null) {
                            // 現在の通知の表示時間が終了したかチェック
                            if (currentTime - currentNotification.startTime >= TEMPORARY_NOTIFICATION_DURATION_MS) {
                                val queue = notificationQueues[uuid]
                                val nextNotification = queue?.poll()

                                if (nextNotification != null) {
                                    // 次の通知を表示
                                    val updatedNotification = nextNotification.copy(startTime = currentTime)
                                    currentNotifications[uuid] = updatedNotification
                                    player.sendActionBar(updatedNotification.message)
                                } else {
                                    // キューが空なので常時表示に戻す
                                    currentNotifications.remove(uuid)
                                    player.sendActionBar(provider.getContent(player))
                                }
                            }
                        } else {
                            // 通知がない場合は常時表示
                            player.sendActionBar(provider.getContent(player))
                        }
                    }
                },
                UPDATE_INTERVAL_TICKS,
                UPDATE_INTERVAL_TICKS,
            )
    }
}
