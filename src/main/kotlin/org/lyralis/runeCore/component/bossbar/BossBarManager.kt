package org.lyralis.runeCore.component.bossbar

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * BossBar の表示を管理するマネージャー．
 */
object BossBarManager {
    private val bossBars = ConcurrentHashMap<UUID, BossBar>()
    private val providers = ConcurrentHashMap<UUID, PersistentBossBarProvider>()

    /**
     * プレイヤーに BossBar プロバイダーを登録し、表示を開始します．
     *
     * @param player プレイヤー
     * @param provider BossBar コンテンツを提供するプロバイダー
     */
    fun registerProvider(
        player: Player,
        provider: PersistentBossBarProvider,
    ) {
        providers[player.uniqueId] = provider

        val bossBar =
            bossBars.computeIfAbsent(player.uniqueId) {
                createBossBar()
            }

        updateBossBar(player, bossBar, provider)

        if (!player.activeBossBars().contains(bossBar)) {
            player.showBossBar(bossBar)
        }
    }

    /**
     * プレイヤーの BossBar を更新します．
     *
     * @param player プレイヤー
     */
    fun update(player: Player) {
        val provider = providers[player.uniqueId] ?: return
        val bossBar = bossBars[player.uniqueId] ?: return

        updateBossBar(player, bossBar, provider)
    }

    /**
     * プレイヤーの BossBar プロバイダーを解除し、表示を停止します．
     *
     * @param player プレイヤー
     */
    fun unregisterProvider(player: Player) {
        providers.remove(player.uniqueId)
        bossBars.remove(player.uniqueId)?.let { bossBar ->
            player.hideBossBar(bossBar)
        }
    }

    /**
     * 全ての BossBar を削除します．
     */
    fun shutdown() {
        bossBars.clear()
        providers.clear()
    }

    private fun createBossBar(): BossBar =
        BossBar.bossBar(
            Component.empty(),
            0f,
            BossBar.Color.GREEN,
            BossBar.Overlay.PROGRESS,
        )

    private fun updateBossBar(
        player: Player,
        bossBar: BossBar,
        provider: PersistentBossBarProvider,
    ) {
        bossBar.name(provider.getTitle(player))
        bossBar.progress(provider.getProgress(player).coerceIn(0f, 1f))
        bossBar.color(provider.getColor(player))
        bossBar.overlay(provider.getOverlay(player))
    }
}
