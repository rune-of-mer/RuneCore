package dev.m1sk9.runeCore

import dev.m1sk9.runeCore.listener.PlayerPresenceListener
import org.bukkit.plugin.java.JavaPlugin

class RuneCore : JavaPlugin() {
    override fun onEnable() {
        logger.info("RuneCore enabled.")
        server.pluginManager.registerEvents(PlayerPresenceListener(), this)
    }

    override fun onDisable() {
        logger.info("RuneCore disabled.")
    }
}
