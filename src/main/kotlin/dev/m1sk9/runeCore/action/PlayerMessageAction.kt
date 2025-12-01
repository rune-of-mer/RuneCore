package dev.m1sk9.runeCore.action

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Server
import org.bukkit.entity.Player

data class PlayerMessageAction(val player: Player, val message: String) {

    private val server: Server = player.server

    fun sendPluginMessage() {
        server.broadcast(Component.text(message).color(NamedTextColor.YELLOW))
    }
}
