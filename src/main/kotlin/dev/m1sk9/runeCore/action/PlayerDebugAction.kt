package dev.m1sk9.runeCore.action

import dev.m1sk9.runeCore.permission.Permission
import dev.m1sk9.runeCore.permission.requirePermissionAll
import org.bukkit.GameMode
import org.bukkit.entity.Player

class PlayerDebugAction(
    val player: Player,
) {
    fun changeGameMode() {
        player.requirePermissionAll {
            +Permission.Admin.DebugMode
            +Permission.Admin.DebugModeSwitchingGameMode
        }

        player.gameMode =
            when (player.gameMode) {
                GameMode.SURVIVAL -> GameMode.CREATIVE
                GameMode.CREATIVE -> GameMode.ADVENTURE
                GameMode.ADVENTURE -> GameMode.SURVIVAL
                else -> GameMode.SURVIVAL
            }
    }
}
