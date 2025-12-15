package dev.m1sk9.runeCore.config

import dev.m1sk9.runeCore.config.model.Config
import dev.m1sk9.runeCore.config.model.DatabaseConfig
import dev.m1sk9.runeCore.config.model.PluginConfig
import dev.m1sk9.runeCore.config.model.PoolConfig
import org.bukkit.configuration.file.FileConfiguration

object ConfigManager {
    private var pluginConfig: Config? = null

    fun load(config: FileConfiguration): Config {
        val loaded =
            Config(
                plugin =
                    PluginConfig(
                        debugMode = config.getBoolean("plugin.debugMode", false),
                    ),
                database =
                    DatabaseConfig(
                        host = config.getString("database.host", "localhost"),
                        port = config.getInt("database.port", 3306),
                        name = config.getString("database.name", "runecore_db"),
                        username = config.getString("database.username", "runecore"),
                        password = config.getString("database.password", "runecore"),
                        pool =
                            PoolConfig(
                                maximumSize = config.getInt("database.pool.maximumSize", 10),
                                minimumIdle = config.getInt("database.pool.minimumIdle", 2),
                                idleTimeout = config.getLong("database.pool.idleTimeout", 60000),
                                connectionTimeout = config.getLong("database.pool.connectionTimeout", 30000),
                            ),
                    ),
            )

        pluginConfig = loaded
        return loaded
    }

    fun get(): Config = pluginConfig ?: throw IllegalStateException("Config not loaded yet")
}
