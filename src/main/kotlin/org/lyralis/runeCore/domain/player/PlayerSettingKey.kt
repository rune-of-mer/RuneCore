package org.lyralis.runeCore.domain.player

/**
 * プレイヤー設定のキー
 * 設定項目を型安全に扱うための enum
 *
 * @param displayName 設定項目の表示名
 * @param description 設定項目の説明
 */
enum class PlayerSettingKey(
    val displayName: String,
    val description: String,
) {
    SHOW_BOSS_BAR("ボスバー表示", "経験値ボスバーの表示/非表示を切り替えます"),
}
