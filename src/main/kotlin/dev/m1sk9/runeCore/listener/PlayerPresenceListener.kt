package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.component.MessageComponent
import dev.m1sk9.runeCore.config.ConfigManager
import dev.m1sk9.runeCore.permission.Permission
import dev.m1sk9.runeCore.permission.hasPermissionAny
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerPresenceListener : Listener {
    private val config = ConfigManager.get()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (
            config.plugin.debugMode &&
            player.hasPermissionAny { +Permission.Admin.DebugMode }
        ) {
            player.sendMessage(MessageComponent("デバッグモードが有効になっているため、デバッグアイテムが利用可能です。").debugMessage())
        }

        event.joinMessage(MessageComponent("${player.name} がログインしました").systemMessage())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player
        event.quitMessage(MessageComponent("${player.name} がログアウトしました").systemMessage())
    }
}
