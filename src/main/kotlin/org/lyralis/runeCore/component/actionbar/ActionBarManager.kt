package org.lyralis.runeCore.component.actionbar

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * ActionBar の表示を管理するマネージャー．
 *
 * 一時的な通知と常時表示を優先度付きで管理します．
 */
object ActionBarManager {
    private lateinit var plugin: Plugin

    private val persistentProviders = ConcurrentHashMap<UUID, PersistentActionBarProvider>()
    private val temporaryNotifications = ConcurrentHashMap<UUID, Long>()
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
        temporaryNotifications.remove(player.uniqueId)
    }

    /**
     * 一時的な通知を表示します．
     *
     * 通知が終わるまで常時表示は一時停止されます．
     *
     * @param player プレイヤー
     * @param message 表示するメッセージ
     */
    fun showTemporaryNotification(
        player: Player,
        message: Component,
    ) {
        temporaryNotifications[player.uniqueId] = System.currentTimeMillis()
        player.sendActionBar(message)
    }

    /**
     * 全ての常時表示を停止し、更新タスクをキャンセルします．
     */
    fun shutdown() {
        updateTask?.cancel()
        updateTask = null
        persistentProviders.clear()
        temporaryNotifications.clear()
    }

    private fun startUpdateTask() {
        updateTask =
            Bukkit.getScheduler().runTaskTimer(
                plugin,
                Runnable {
                    val currentTime = System.currentTimeMillis()

                    for ((uuid, provider) in persistentProviders) {
                        val player = Bukkit.getPlayer(uuid) ?: continue

                        val notificationTime = temporaryNotifications[uuid]
                        if (notificationTime != null) {
                            if (currentTime - notificationTime >= TEMPORARY_NOTIFICATION_DURATION_MS) {
                                temporaryNotifications.remove(uuid)
                                player.sendActionBar(provider.getContent(player))
                            }
                        } else {
                            player.sendActionBar(provider.getContent(player))
                        }
                    }
                },
                UPDATE_INTERVAL_TICKS,
                UPDATE_INTERVAL_TICKS,
            )
    }
}
