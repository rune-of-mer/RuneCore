package org.lyralis.runeCore.config

import org.bukkit.configuration.file.FileConfiguration
import org.lyralis.runeCore.config.model.Config
import org.lyralis.runeCore.config.model.DatabaseConfig
import org.lyralis.runeCore.config.model.DistanceTier
import org.lyralis.runeCore.config.model.PluginConfig
import org.lyralis.runeCore.config.model.PoolConfig
import org.lyralis.runeCore.config.model.TeleportConfig
import org.lyralis.runeCore.config.model.TeleportCostConfig

object ConfigManager {
    private var pluginConfig: Config? = null

    /**
     * コンフィグを読み込みます．
     *
     * @param config Spigot/Paper API の Config [FileConfiguration]
     * @return [Config] RuneCore の内部で使用するコンフィグのモデル
     */
    fun load(config: FileConfiguration): Config {
        val loaded =
            Config(
                plugin =
                    PluginConfig(
                        patchNoteURL = config.getString("plugin.patchNoteURL", "https://example.com")!!,
                        motd = config.getStringList("plugin.motd"),
                        // noKnownPlayers
                        firstMotd = config.getStringList("plugin.noKnownPlayers.firstMotd"),
                        tutorialRune = config.getLong("plugin.noKnownPlayers.tutorialRune"),
                        tutorialExp = config.getLong("plugin.noKnownPlayers.tutorialExp"),
                    ),
                database =
                    DatabaseConfig(
                        host = config.getString("database.host")!!,
                        port = config.getInt("database.port"),
                        name = config.getString("database.name")!!,
                        username = config.getString("database.username")!!,
                        password = config.getString("database.password")!!,
                        pool =
                            PoolConfig(
                                maximumSize = config.getInt("database.pool.maximumSize", 10),
                                minimumIdle = config.getInt("database.pool.minimumIdle", 2),
                                idleTimeout = config.getLong("database.pool.idleTimeout", 60000),
                                connectionTimeout = config.getLong("database.pool.connectionTimeout", 30000),
                            ),
                    ),
                teleport = loadTeleportConfig(config),
            )

        pluginConfig = loaded
        return loaded
    }

    /**
     * コンフィグを取得します．このメソッドを呼び出すには [org.lyralis.runeCore.RuneCore] で初期化処理を済ましておく必要があります．
     *
     *
     *
     * ```
     * private val config = ConfigManager.get()
     * ```
     *
     *
     * @return [Config] RuneCore の内部で使用するコンフィグデータ
     * @throws [IllegalStateException] コンフィグの初期化処理がされておらず，コンフィグファイルが読み込まれていない場合にスローされます．先に [load] を実行する必要があります．
     */
    fun get(): Config = pluginConfig ?: throw IllegalStateException("Config not loaded yet")

    private fun loadTeleportConfig(config: FileConfiguration): TeleportConfig {
        val requestTimeoutSeconds = config.getLong("teleport.requestTimeoutSeconds", 60)
        val defaultWarpSlots = config.getInt("teleport.defaultWarpSlots", 3)

        val maxCost = config.getLong("teleport.costs.maxCost", 1000).toULong()
        val crossWorldBaseCost = config.getLong("teleport.costs.crossWorldBaseCost", 500).toULong()

        val distanceTiers =
            loadDistanceTiers(config, "teleport.costs.distanceTiers")
                ?: TeleportCostConfig.defaultDistanceTiers()
        val crossWorldDistanceTiers =
            loadDistanceTiers(config, "teleport.costs.crossWorldDistanceTiers")
                ?: TeleportCostConfig.defaultCrossWorldDistanceTiers()

        return TeleportConfig(
            requestTimeoutSeconds = requestTimeoutSeconds,
            defaultWarpSlots = defaultWarpSlots,
            costs =
                TeleportCostConfig(
                    maxCost = maxCost,
                    crossWorldBaseCost = crossWorldBaseCost,
                    distanceTiers = distanceTiers,
                    crossWorldDistanceTiers = crossWorldDistanceTiers,
                ),
        )
    }

    private fun loadDistanceTiers(
        config: FileConfiguration,
        path: String,
    ): List<DistanceTier>? {
        val section = config.getConfigurationSection(path) ?: return null
        return section
            .getKeys(false)
            .mapNotNull { key ->
                val tierSection = section.getConfigurationSection(key) ?: return@mapNotNull null
                DistanceTier(
                    minDistance = tierSection.getDouble("min", 0.0),
                    maxDistance = tierSection.getDouble("max", Double.MAX_VALUE),
                    cost = tierSection.getLong("cost", 0).toULong(),
                )
            }.sortedBy { it.minDistance }
    }
}
