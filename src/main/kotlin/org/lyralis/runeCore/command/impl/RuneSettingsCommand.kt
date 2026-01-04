package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.component.bossbar.BossBarManager
import org.lyralis.runeCore.component.bossbar.ExperienceBossBarProvider
import org.lyralis.runeCore.domain.player.PlayerSettingKey
import org.lyralis.runeCore.domain.settings.SettingsService
import org.lyralis.runeCore.gui.impl.settings.SettingsGui
import org.lyralis.runeCore.gui.toCommandResult

@PlayerOnlyCommand
class RuneSettingsCommand(
    private val settingsService: SettingsService,
    private val experienceBossBarProvider: ExperienceBossBarProvider,
) : RuneCommand {
    override val name = "settings"
    override val description = "プレイヤー設定を変更します"
    override val aliases = listOf("setting", "config", "preferences")

    private val settingsGui by lazy { SettingsGui(settingsService, experienceBossBarProvider) }

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val args = context.args

        if (args.isEmpty()) {
            return settingsGui.open(player).toCommandResult("設定画面を開きました")
        }

        return when (args[0].lowercase()) {
            "bossbar", "boss", "bar", "expbar" -> toggleBossBar(context)
            else -> CommandResult.Failure.InvalidArgument("不明な設定項目です: ${args[0]}")
        }
    }

    private fun toggleBossBar(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        val newValue =
            settingsService.toggleSetting(player.uniqueId, PlayerSettingKey.SHOW_BOSS_BAR)
                ?: return CommandResult.Failure.ExecutionFailed("設定の変更に失敗しました")

        if (newValue) {
            BossBarManager.registerProvider(player, experienceBossBarProvider)
        } else {
            BossBarManager.unregisterProvider(player)
        }

        val statusText = if (newValue) "§a表示" else "§c非表示"
        return CommandResult.Success("経験値ボスバーを$statusText§rに変更しました")
    }

    override fun suggest(context: SuggestionContext): List<String> {
        if (context.args.size == 1) {
            return listOf("bossbar").filter { it.startsWith(context.args[0].lowercase()) }
        }
        return emptyList()
    }
}
