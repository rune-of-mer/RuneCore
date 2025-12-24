package org.lyralis.runeCore

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.lyralis.runeCore.command.impl.RuneCustomGiveCommand
import org.lyralis.runeCore.command.impl.RuneDiceCommand
import org.lyralis.runeCore.command.impl.RuneLogoutCommand
import org.lyralis.runeCore.command.impl.RuneMenuCommand
import org.lyralis.runeCore.command.impl.RunePatchNoteCommand
import org.lyralis.runeCore.command.impl.RunePayCommand
import org.lyralis.runeCore.command.impl.RunePlayTimeCommand
import org.lyralis.runeCore.command.impl.RunePlayerInfoCommand
import org.lyralis.runeCore.command.impl.RunePlayerListCommand
import org.lyralis.runeCore.command.impl.RuneSettingsCommand
import org.lyralis.runeCore.command.impl.RuneShopCommand
import org.lyralis.runeCore.command.impl.RuneTrashCommand
import org.lyralis.runeCore.command.impl.experience.RuneExperienceCommand
import org.lyralis.runeCore.command.impl.level.RuneLevelCommand
import org.lyralis.runeCore.command.impl.money.RuneMoneyCommand
import org.lyralis.runeCore.command.impl.teleport.RuneTpaCommand
import org.lyralis.runeCore.command.impl.teleport.RuneTpcCommand
import org.lyralis.runeCore.command.impl.teleport.RuneTppCommand
import org.lyralis.runeCore.command.impl.gacha.RuneGachaAdminCommand
import org.lyralis.runeCore.command.impl.gacha.RuneGachaCommand
import org.lyralis.runeCore.command.impl.warp.RuneWarpCommand
import org.lyralis.runeCore.command.impl.world.RuneWorldCommand
import org.lyralis.runeCore.command.register.CommandRegistry
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.bossbar.BossBarManager
import org.lyralis.runeCore.component.bossbar.ExperienceBossBarProvider
import org.lyralis.runeCore.config.ConfigManager
import org.lyralis.runeCore.database.DatabaseManager
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.impl.gacha.GachaService
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.impl.settings.SettingsService
import org.lyralis.runeCore.database.impl.teleport.TeleportCostCalculator
import org.lyralis.runeCore.database.repository.GachaRepository
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.SettingsRepository
import org.lyralis.runeCore.database.repository.StatsRepository
import org.lyralis.runeCore.database.repository.WarpPointRepository
import org.lyralis.runeCore.gui.cache.PlayerHeadCacheCleanupTask
import org.lyralis.runeCore.gui.cache.PlayerHeadCacheManager
import org.lyralis.runeCore.gui.impl.shop.ShopMainGui
import org.lyralis.runeCore.item.ItemRegistry
import org.lyralis.runeCore.item.impl.debug.DebugCompassItem
import org.lyralis.runeCore.listener.CustomItemInteractListener
import org.lyralis.runeCore.listener.GachaInventoryListener
import org.lyralis.runeCore.listener.PlayerExperienceListener
import org.lyralis.runeCore.listener.PlayerLoginListener
import org.lyralis.runeCore.listener.PlayerPresenceListener
import org.lyralis.runeCore.listener.PlayerWorldTeleportListener
import org.lyralis.runeCore.listener.ShopChatInputListener
import org.lyralis.runeCore.listener.TrashInventoryListener
import org.lyralis.runeCore.teleport.TeleportRequestManager
import org.lyralis.runeCore.teleport.TeleportService
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
    private lateinit var shopMainGui: ShopMainGui
    private lateinit var warpPointRepository: WarpPointRepository
    private lateinit var teleportRequestManager: TeleportRequestManager
    private lateinit var teleportCostCalculator: TeleportCostCalculator
    private lateinit var teleportService: TeleportService
    private lateinit var gachaRepository: GachaRepository
    private lateinit var gachaService: GachaService

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
        shopMainGui = ShopMainGui(moneyService)

        // テレポートシステムの初期化
        warpPointRepository = WarpPointRepository()
        teleportCostCalculator = TeleportCostCalculator(config.teleport.costs)
        teleportService = TeleportService(moneyService, logger)
        teleportRequestManager = TeleportRequestManager(this, config.teleport.requestTimeoutSeconds)
        teleportRequestManager.start()

        // ガチャシステムの初期化
        gachaRepository = GachaRepository()
        gachaService = GachaService(gachaRepository, logger)
        gachaService.initializeDefaultEvents()

        ActionBarManager.initialize(this)

        CommandRegistry(this)
            .register(RuneExperienceCommand(experienceService))
            .register(RuneMoneyCommand(moneyService))
            .register(RuneCustomGiveCommand())
            .register(RuneDiceCommand())
            .register(RuneLevelCommand(moneyService, experienceService))
            .register(RuneLogoutCommand())
            .register(RuneMenuCommand(experienceService, moneyService))
            .register(RunePatchNoteCommand())
            .register(RunePayCommand(moneyService))
            .register(RunePlayerInfoCommand(experienceService, moneyService))
            .register(RunePlayerListCommand(playerRepository))
            .register(RunePlayTimeCommand())
            .register(RuneSettingsCommand(settingsService, experienceBossBarProvider))
            .register(RuneShopCommand(shopMainGui))
            .register(RuneTrashCommand())
            // テレポートコマンド
            .register(RuneTppCommand(teleportRequestManager, teleportCostCalculator, moneyService))
            .register(RuneTpaCommand(teleportRequestManager, teleportService, moneyService))
            .register(RuneTpcCommand(teleportRequestManager))
            .register(
                RuneWarpCommand(
                    warpPointRepository,
                    teleportService,
                    teleportCostCalculator,
                    moneyService,
                    config.teleport,
                ),
            )
            // ワールドテレポートコマンド
            .register(
                RuneWorldCommand(
                    config.world,
                    teleportService,
                    moneyService,
                    config.teleport.costs.crossWorldBaseCost,
                ),
            )
            // ガチャコマンド
            .register(RuneGachaCommand(gachaService))
            .register(RuneGachaAdminCommand(gachaService))
            .registerAll(lifecycleManager)

        server.pluginManager.registerEvents(CustomItemInteractListener(), this)
        server.pluginManager.registerEvents(PlayerExperienceListener(experienceService, moneyService), this)
        server.pluginManager.registerEvents(PlayerLoginListener(playerRepository, logger), this)
        server.pluginManager.registerEvents(
            PlayerPresenceListener(experienceService, moneyService, settingsService, experienceBossBarProvider),
            this,
        )
        server.pluginManager.registerEvents(
            PlayerWorldTeleportListener(),
            this,
        )
        server.pluginManager.registerEvents(TrashInventoryListener(this, moneyService), this)
        server.pluginManager.registerEvents(ShopChatInputListener(this, moneyService, shopMainGui), this)
        server.pluginManager.registerEvents(GachaInventoryListener(), this)

        headCacheCleanupTask = PlayerHeadCacheCleanupTask(this, logger)
        headCacheCleanupTask.start()

        logger.info("RuneCore enabled.")
    }

    override fun onDisable() {
        // テレポートリクエストマネージャーを停止
        if (::teleportRequestManager.isInitialized) {
            teleportRequestManager.stop()
        }

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
