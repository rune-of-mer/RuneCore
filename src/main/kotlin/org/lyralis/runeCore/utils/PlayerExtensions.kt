package org.lyralis.runeCore.utils

import org.bukkit.entity.Player

/**
 * プレイヤーの累積プレイ時間をフォーマットして取得する拡張関数
 *
 * @receiver Player プレイヤーオブジェクト
 * @return String フォーマットされたプレイ時間 (例: "01時間23分45秒")
 */
fun Player.getFormattedPlayTime(): String {
    val playTimeTicks: Long = this.playerTime

    val totalSeconds = playTimeTicks / 20
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d時間%02d分%02d秒", hours, minutes, seconds)
}
