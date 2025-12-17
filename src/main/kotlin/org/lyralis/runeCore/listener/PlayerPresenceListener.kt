package org.lyralis.runeCore.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.lyralis.runeCore.component.debugMessage
import org.lyralis.runeCore.component.systemMessage
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.permission.Permission
import org.lyralis.runeCore.permission.hasPermissionAny

class PlayerPresenceListener : Listener {
    private val config = ConfigManager.get()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (
            config.plugin.debugMode &&
            player.hasPermissionAny { +Permission.Admin.DebugMode }
        ) {
            player.sendMessage("デバッグモードが有効になっているため、デバッグアイテムが利用可能です。".debugMessage())
        }

        event.joinMessage("${player.name} がログインしました".systemMessage())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player
        event.quitMessage("${player.name} がログアウトしました".systemMessage())
    }
}
