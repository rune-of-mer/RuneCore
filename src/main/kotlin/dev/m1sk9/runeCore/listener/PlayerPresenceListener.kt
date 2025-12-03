package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.RuneCore
import dev.m1sk9.runeCore.component.MessageComponent
import dev.m1sk9.runeCore.config.ConfigManager
import dev.m1sk9.runeCore.player.permission.PlayerPermission
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * Listener for player join/quit events.
 * Handles actions to be performed when a player joins the server.
 * Processes to be executed before the player logs in should be described in the PlayerLoginListener. Implement the database.
 */
class PlayerPresenceListener : Listener {

    private val config = ConfigManager.get()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (config.plugin.debugMode && PlayerPermission.Admin.DebugMode.has(player)) {
            player.sendMessage(MessageComponent("デバッグモードが有効になっているため，必要な権限が付与されていればデバッグコマンドを使用できます").debugMessage())
        }

        event.joinMessage(MessageComponent("${player.name} がログインしました").systemMessage())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player
        event.quitMessage(MessageComponent("${player.name} がログアウトしました").systemMessage())
    }
}
