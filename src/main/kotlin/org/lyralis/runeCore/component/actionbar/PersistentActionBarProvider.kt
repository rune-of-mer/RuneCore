package org.lyralis.runeCore.component.actionbar

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * ActionBar の常時表示コンテンツを提供するインターフェース．
 */
fun interface PersistentActionBarProvider {
    /**
     * 表示するコンテンツを取得します．
     *
     * この関数は毎秒呼び出されるため、軽量に保つ必要があります．
     *
     * @param player プレイヤー
     * @return 表示する Component
     */
    fun getContent(player: Player): Component
}
