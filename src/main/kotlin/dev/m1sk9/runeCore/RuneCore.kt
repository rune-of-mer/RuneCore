package dev.m1sk9.runeCore

import dev.m1sk9.runeCore.config.ConfigManager
import dev.m1sk9.runeCore.listener.PlayerDebugModeListener
import dev.m1sk9.runeCore.listener.PlayerPresenceListener
import org.bukkit.plugin.java.JavaPlugin

class RuneCore : JavaPlugin() {
    override fun onEnable() {
        saveDefaultConfig()
        val config = ConfigManager.load(config)

        if (config.plugin.debugMode) {
            logger.warning("Debug mode enabled!")
            server.pluginManager.registerEvents(PlayerDebugModeListener(), this)
        }

        server.pluginManager.registerEvents(PlayerPresenceListener(), this)

        logger.info("RuneCore enabled.")
    }

    override fun onDisable() {
        logger.info("RuneCore disabled.")
    }
}
