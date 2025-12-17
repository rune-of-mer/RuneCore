package dev.m1sk9.runeCore

import dev.m1sk9.runeCore.command.impl.RuneDiceCommand
import dev.m1sk9.runeCore.command.impl.RuneLogoutCommand
import dev.m1sk9.runeCore.command.impl.RunePatchNoteCommand
import dev.m1sk9.runeCore.command.impl.RunePlayTimeCommand
import dev.m1sk9.runeCore.command.impl.RunePlayerListCommand
import dev.m1sk9.runeCore.command.register.CommandRegistry
import dev.m1sk9.runeCore.config.ConfigManager
import dev.m1sk9.runeCore.database.DatabaseManager
import dev.m1sk9.runeCore.database.repository.PlayerRepository
import dev.m1sk9.runeCore.database.repository.StatsRepository
import dev.m1sk9.runeCore.listener.PlayerDebugModeListener
import dev.m1sk9.runeCore.listener.PlayerLoginListener
import dev.m1sk9.runeCore.listener.PlayerPresenceListener
import dev.m1sk9.runeCore.listener.PlayerStatsListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

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
