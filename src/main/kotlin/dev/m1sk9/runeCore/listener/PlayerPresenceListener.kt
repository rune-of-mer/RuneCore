package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.RuneCore
import dev.m1sk9.runeCore.component.MessageComponent
import org.bukkit.Server
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
    private val plugin = JavaPlugin.getPlugin(RuneCore::class.java)
    private val maxPlayers = plugin.server.maxPlayers

    private fun showCurrentPlayerCount(server: Server): String = "(${server.onlinePlayers.size}/$maxPlayers)"

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val server = player.server

        event.joinMessage(MessageComponent("${player.name} がログインしました ${showCurrentPlayerCount(server)}").systemMessage())
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player
        val server = player.server

        event.quitMessage(MessageComponent("${player.name} がログアウトしました ${showCurrentPlayerCount(server)}").systemMessage())
    }
}
