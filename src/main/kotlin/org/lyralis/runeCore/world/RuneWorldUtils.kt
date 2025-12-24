package org.lyralis.runeCore.world

import org.bukkit.World
import org.lyralis.runeCore.config.ConfigManager

object RuneWorldUtils {
    private val config = ConfigManager.get()

    fun isExecute(currentWorld: World): Boolean = currentWorld.name == config.world.dz.name
}
