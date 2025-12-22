package org.lyralis.runeCore.component.bossbar

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * BossBar の常時表示コンテンツを提供するインターフェース．
 */
interface PersistentBossBarProvider {
    /**
     * 表示するタイトルを取得します．
     *
     * @param player プレイヤー
     * @return 表示する Component
     */
    fun getTitle(player: Player): Component

    /**
     * 表示する進捗を取得します．
     *
     * @param player プレイヤー
     * @return 0.0f から 1.0f の進捗値
     */
    fun getProgress(player: Player): Float

    /**
     * 表示する色を取得します．
     *
     * @param player プレイヤー
     * @return BossBar の色
     */
    fun getColor(player: Player): BossBar.Color

    /**
     * 表示するオーバーレイを取得します．
     * デフォルトは PROGRESS です．
     *
     * @param player プレイヤー
     * @return BossBar のオーバーレイ
     */
    fun getOverlay(player: Player): BossBar.Overlay = BossBar.Overlay.PROGRESS
}
