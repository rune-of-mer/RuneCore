package org.lyralis.runeCore.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.lyralis.runeCore.config.model.DatabaseConfig
import org.lyralis.runeCore.database.table.PlayerStats
import org.lyralis.runeCore.database.table.Players
import java.util.logging.Logger

class DatabaseManager(
    private val config: DatabaseConfig,
    private val logger: Logger,
) {
    private var dataSource: HikariDataSource? = null

    fun connect() {
        logger.info("Connecting to database...")

        val hikariConfig =
            HikariConfig().apply {
                driverClassName = "org.mariadb.jdbc.Driver"
                jdbcUrl = "jdbc:mariadb://${config.host}:${config.port}/${config.name}"
                username = config.username
                password = config.password
                maximumPoolSize = config.pool.maximumSize
                minimumIdle = config.pool.minimumIdle
                idleTimeout = config.pool.idleTimeout
                connectionTimeout = config.pool.connectionTimeout

                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            }

        dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource!!)

        transaction {
            SchemaUtils.create(
                Players,
                PlayerStats,
            )
        }

        logger.info("Database connected.")
    }

    fun disconnect() {
        dataSource?.close()
        logger.info("Database disconnected.")
    }
}
