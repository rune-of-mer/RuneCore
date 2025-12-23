package org.lyralis.runeCore.gui.impl.settings

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.bossbar.BossBarManager
import org.lyralis.runeCore.component.bossbar.ExperienceBossBarProvider
import org.lyralis.runeCore.database.impl.settings.SettingsService
import org.lyralis.runeCore.database.model.PlayerSettingKey
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * プレイヤー設定GUI
 */
class SettingsGui(
    private val settingsService: SettingsService,
    private val experienceBossBarProvider: ExperienceBossBarProvider,
) {
    /**
     * 設定GUIを開く
     *
     * @param player 対象プレイヤー
     * @return GUI操作結果
     */
    fun open(player: Player): GuiResult<Unit> {
        val settings = settingsService.getSettings(player.uniqueId)

        return player.openGui {
            title = "設定"
            rows = 3

            structure {
                +"# # # # # # # # #"
                +"# . . A . . . . #"
                +"# # # # # # # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)

            item('A') {
                customItem =
                    Material.EXPERIENCE_BOTTLE.asGuiItem {
                        displayName = "§e経験値ボスバー"
                        lore(buildBossBarLore(settings.showBossBar))
                        glowing = settings.showBossBar
                    }

                onClick { action ->
                    toggleBossBar(action.player)
                    open(action.player)
                    GuiResult.Success(Unit)
                }
            }
        }
    }

    private fun buildBossBarLore(enabled: Boolean): List<String> =
        listOf(
            "",
            if (enabled) "§a✔ 表示中" else "§c✖ 非表示",
            "",
            "§7クリックで切り替え",
        )

    private fun toggleBossBar(player: Player) {
        val newValue = settingsService.toggleSetting(player.uniqueId, PlayerSettingKey.SHOW_BOSS_BAR)

        if (newValue == true) {
            BossBarManager.registerProvider(player, experienceBossBarProvider)
        } else {
            BossBarManager.unregisterProvider(player)
        }
    }
}
