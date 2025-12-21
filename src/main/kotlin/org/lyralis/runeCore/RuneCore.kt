package org.lyralis.runeCore

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.lyralis.runeCore.command.impl.RuneCustomGiveCommand
import org.lyralis.runeCore.command.impl.RuneCustomItemIDCommand
import org.lyralis.runeCore.command.impl.RuneDiceCommand
import org.lyralis.runeCore.command.impl.RuneLevelCommand
import org.lyralis.runeCore.command.impl.RuneLogoutCommand
import org.lyralis.runeCore.command.impl.RunePatchNoteCommand
import org.lyralis.runeCore.command.impl.RunePlayTimeCommand
import org.lyralis.runeCore.command.impl.RunePlayerListCommand
import org.lyralis.runeCore.command.impl.experience.RuneExperienceCommand
import org.lyralis.runeCore.command.register.CommandRegistry
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.database.DatabaseManager
import org.lyralis.runeCore.database.impl.experience.ExperienceBossBarManager
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.StatsRepository
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.item.impl.debug.DebugCompassItem
import org.lyralis.runeCore.listener.CustomItemInteractListener
import org.lyralis.runeCore.listener.PlayerExperienceListener
import org.lyralis.runeCore.listener.PlayerLoginListener
import org.lyralis.runeCore.listener.PlayerPresenceListener

class
RuneCore : JavaPlugin() {
    private lateinit var databaseManager: DatabaseManager
    private lateinit var playerRepository: PlayerRepository
    private lateinit var statsRepository: StatsRepository
    private lateinit var experienceService: ExperienceService

    override fun onEnable() {
        saveDefaultConfig()
        val config = ConfigManager.load(config)

        databaseManager = DatabaseManager(config.database, logger)
        try {
            databaseManager.connect()
        } catch (e: Exception) {
            logger.severe("Failed to connect to database. Shutting down...: ${e.message}")
            Bukkit.shutdown()
            return
        }

        ItemRegistry.initialize(this)
        ItemRegistry.registerAll(
            DebugCompassItem,
        )
        logger.info("Registered ${ItemRegistry.getAllItems().size} items!")

        playerRepository = PlayerRepository()
        statsRepository = StatsRepository()
        experienceService = ExperienceService(playerRepository, logger)

        CommandRegistry(this)
            .register(RuneExperienceCommand(experienceService))
            .register(RuneCustomGiveCommand())
            .register(RuneCustomItemIDCommand())
            .register(RuneDiceCommand())
            .register(RuneLevelCommand(playerRepository, logger))
            .register(RuneLogoutCommand())
            .register(RunePatchNoteCommand())
            .register(RunePlayerListCommand())
            .register(RunePlayTimeCommand())
            .registerAll(lifecycleManager)

        server.pluginManager.registerEvents(CustomItemInteractListener(), this)
        server.pluginManager.registerEvents(PlayerExperienceListener(experienceService), this)
        server.pluginManager.registerEvents(PlayerLoginListener(playerRepository, logger), this)
        server.pluginManager.registerEvents(PlayerPresenceListener(experienceService), this)

        logger.info("RuneCore enabled.")
    }

    override fun onDisable() {
        if (::databaseManager.isInitialized) {
            databaseManager.disconnect()
        }

        ExperienceBossBarManager.removeAllBossBars()
        experienceService.clearAllCache()

        logger.info("RuneCore disabled.")
    }
}
