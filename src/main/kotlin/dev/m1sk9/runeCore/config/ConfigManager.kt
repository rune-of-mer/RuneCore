package dev.m1sk9.runeCore.config

import dev.m1sk9.runeCore.config.model.Config
import dev.m1sk9.runeCore.config.model.PluginConfig
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
            )

        pluginConfig = loaded
        return loaded
    }

    fun get(): Config = pluginConfig ?: throw IllegalStateException("Config not loaded yet")
}
