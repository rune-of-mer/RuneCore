package dev.m1sk9.runeCore.action

import dev.m1sk9.runeCore.component.MessageComponent
import org.bukkit.Server
import org.bukkit.entity.Player

data class PlayerMessageAction(
    val player: Player,
    val message: String,
) {
    private val server: Server = player.server

    fun sendPluginMessage() {
        server.broadcast(MessageComponent(message).systemMessage())
    }
}
