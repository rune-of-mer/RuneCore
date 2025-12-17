package org.lyralis.runeCore

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.lyralis.runeCore.command.impl.RuneDiceCommand
import org.lyralis.runeCore.command.impl.RuneLogoutCommand
import org.lyralis.runeCore.command.impl.RunePatchNoteCommand
import org.lyralis.runeCore.command.impl.RunePlayTimeCommand
import org.lyralis.runeCore.command.impl.RunePlayerListCommand
import org.lyralis.runeCore.command.register.CommandRegistry
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.database.DatabaseManager
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.StatsRepository
import org.lyralis.runeCore.listener.PlayerDebugModeListener
import org.lyralis.runeCore.listener.PlayerLoginListener
import org.lyralis.runeCore.listener.PlayerPresenceListener
import org.lyralis.runeCore.listener.PlayerStatsListener

class RuneCore : JavaPlugin() {
    private lateinit var databaseManager: DatabaseManager
    private lateinit var playerRepository: PlayerRepository
    private lateinit var statsRepository: StatsRepository

    override fun onEnable() {
        saveDefaultConfig()
        val config = ConfigManager.load(config)

        if (config.plugin.debugMode) {
            logger.warning("Debug mode enabled.")
            server.pluginManager.registerEvents(PlayerDebugModeListener(), this)
        }

        databaseManager = DatabaseManager(config.database, logger)
        try {
            databaseManager.connect()
        } catch (e: Exception) {
            logger.severe("Failed to connect to database. Shutting down...: ${e.message}")
            Bukkit.shutdown()
            return
        }

        playerRepository = PlayerRepository()
        statsRepository = StatsRepository()

        CommandRegistry(this)
            .register(RuneDiceCommand())
            .register(RuneLogoutCommand())
            .register(RunePatchNoteCommand())
            .register(RunePlayerListCommand())
            .register(RunePlayTimeCommand())
            .registerAll(lifecycleManager)

        server.pluginManager.registerEvents(PlayerLoginListener(playerRepository, logger), this)
        server.pluginManager.registerEvents(PlayerPresenceListener(), this)
        server.pluginManager.registerEvents(PlayerStatsListener(statsRepository, logger), this)

        logger.info("RuneCore enabled.")
    }

    override fun onDisable() {
        if (::databaseManager.isInitialized) {
            databaseManager.disconnect()
        }

        logger.info("RuneCore disabled.")
    }
}
