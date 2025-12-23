package org.lyralis.runeCore

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.lyralis.runeCore.command.impl.RuneCustomGiveCommand
import org.lyralis.runeCore.command.impl.RuneDiceCommand
import org.lyralis.runeCore.command.impl.RuneLevelCommand
import org.lyralis.runeCore.command.impl.RuneLogoutCommand
import org.lyralis.runeCore.command.impl.RuneMenuCommand
import org.lyralis.runeCore.command.impl.RunePatchNoteCommand
import org.lyralis.runeCore.command.impl.RunePlayTimeCommand
import org.lyralis.runeCore.command.impl.RunePlayerInfoCommand
import org.lyralis.runeCore.command.impl.RunePlayerListCommand
import org.lyralis.runeCore.command.impl.RuneTrashCommand
import org.lyralis.runeCore.command.impl.experience.RuneExperienceCommand
import org.lyralis.runeCore.command.impl.money.RuneMoneyCommand
import org.lyralis.runeCore.command.impl.settings.RuneSettingsCommand
import org.lyralis.runeCore.command.register.CommandRegistry
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.bossbar.BossBarManager
import org.lyralis.runeCore.component.bossbar.ExperienceBossBarProvider
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.database.DatabaseManager
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.impl.settings.SettingsService
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.SettingsRepository
import org.lyralis.runeCore.database.repository.StatsRepository
import org.lyralis.runeCore.gui.cache.PlayerHeadCacheCleanupTask
import org.lyralis.runeCore.gui.cache.PlayerHeadCacheManager
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.item.impl.debug.DebugCompassItem
import org.lyralis.runeCore.listener.CustomItemInteractListener
import org.lyralis.runeCore.listener.PlayerExperienceListener
import org.lyralis.runeCore.listener.PlayerLoginListener
import org.lyralis.runeCore.listener.PlayerPresenceListener
import org.lyralis.runeCore.listener.TrashInventoryListener
import xyz.xenondevs.invui.InvUI

class RuneCore : JavaPlugin() {
    private lateinit var databaseManager: DatabaseManager
    private lateinit var playerRepository: PlayerRepository
    private lateinit var statsRepository: StatsRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var experienceService: ExperienceService
    private lateinit var moneyService: MoneyService
    private lateinit var settingsService: SettingsService
    private lateinit var experienceBossBarProvider: ExperienceBossBarProvider
    private lateinit var headCacheCleanupTask: PlayerHeadCacheCleanupTask

    override fun onEnable() {
        // InvUI のプラグインインスタンスを設定（Paper 1.20.5+ で必要）
        InvUI.getInstance().setPlugin(this)

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
        settingsRepository = SettingsRepository()
        experienceService = ExperienceService(playerRepository, logger)
        moneyService = MoneyService(playerRepository, logger)
        settingsService = SettingsService(settingsRepository, logger)
        experienceBossBarProvider =
            ExperienceBossBarProvider(
                experienceProvider = { uuid -> experienceService.getExperience(uuid) },
                levelProvider = { uuid -> experienceService.getLevel(uuid) },
            )

        ActionBarManager.initialize(this)

        CommandRegistry(this)
            .register(RuneExperienceCommand(experienceService))
            .register(RuneMoneyCommand(moneyService))
            .register(RuneCustomGiveCommand())
            .register(RuneDiceCommand())
            .register(RuneLevelCommand(playerRepository, logger))
            .register(RuneLogoutCommand())
            .register(RuneMenuCommand(experienceService, moneyService))
            .register(RunePatchNoteCommand())
            .register(RunePlayerInfoCommand(experienceService, moneyService))
            .register(RunePlayerListCommand(playerRepository))
            .register(RunePlayTimeCommand())
            .register(RuneSettingsCommand(settingsService, experienceBossBarProvider))
            .register(RuneTrashCommand())
            .registerAll(lifecycleManager)

        server.pluginManager.registerEvents(CustomItemInteractListener(), this)
        server.pluginManager.registerEvents(PlayerExperienceListener(experienceService, moneyService), this)
        server.pluginManager.registerEvents(PlayerLoginListener(playerRepository, logger), this)
        server.pluginManager.registerEvents(
            PlayerPresenceListener(experienceService, moneyService, settingsService, experienceBossBarProvider),
            this,
        )
        server.pluginManager.registerEvents(TrashInventoryListener(this, moneyService), this)

        headCacheCleanupTask = PlayerHeadCacheCleanupTask(this, logger)
        headCacheCleanupTask.start()

        logger.info("RuneCore enabled.")
    }

    override fun onDisable() {
        if (::databaseManager.isInitialized) {
            databaseManager.disconnect()
        }

        BossBarManager.shutdown()
        experienceService.clearAllCache()

        ActionBarManager.shutdown()
        moneyService.clearAllCache()
        settingsService.clearAllCache()

        PlayerHeadCacheManager.clearAllCache()

        if (::headCacheCleanupTask.isInitialized) {
            headCacheCleanupTask.stop()
        }

        logger.info("RuneCore disabled.")
    }
}
