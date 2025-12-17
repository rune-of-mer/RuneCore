package org.lyralis.runeCore.config

import org.bukkit.configuration.file.FileConfiguration
import org.lyralis.runeCore.config.model.Config
import org.lyralis.runeCore.config.model.DatabaseConfig
import org.lyralis.runeCore.config.model.PluginConfig
import org.lyralis.runeCore.config.model.PoolConfig

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
                        debugMode = config.getBoolean("plugin.debugMode", false),
                        patchNoteURL = config.getString("plugin.patchNoteURL", "https://example.com")!!,
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
}
