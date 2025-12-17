package dev.m1sk9.runeCore.listener

import dev.m1sk9.runeCore.component.action.PlayerDebugAction
import dev.m1sk9.runeCore.component.errorMessage
import dev.m1sk9.runeCore.component.systemMessage
import dev.m1sk9.runeCore.permission.RequirePermissionException
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class PlayerDebugModeListener : Listener {
    @EventHandler
    fun onDebugMode(event: PlayerInteractEvent) {
        val player = event.player
        if (!player.isSneaking) return

        val inHandItem = event.item ?: return
        when (inHandItem.type) {
            Material.COMPASS -> {
                try {
                    PlayerDebugAction(player).changeGameMode()
                    player.sendActionBar("ゲームモードを変更しました: ${player.gameMode}".systemMessage())
                } catch (e: RequirePermissionException) {
                    player.sendMessage((e.message ?: "権限が不足しています").errorMessage())
                }
            }
            else -> return
        }
    }
}
