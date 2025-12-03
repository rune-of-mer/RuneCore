package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.action.PlayerMessageAction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener for player join/quit events.
 * Handles actions to be performed when a player joins the server.
 * Processes to be executed before the player logs in should be described in the PlayerLoginListener. Implement the database.
 */
class PlayerPresenceListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        PlayerMessageAction(player, "${player.name} がログインしました").sendPluginMessage()
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player
        PlayerMessageAction(player, "${player.name} がログアウトしました").sendPluginMessage()
    }
}
