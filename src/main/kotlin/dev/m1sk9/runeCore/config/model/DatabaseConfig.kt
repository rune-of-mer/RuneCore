package dev.m1sk9.runeCore.config.model

data class DatabaseConfig(
    val host: String? = "localhost",
    val port: Int = 3306,
    val name: String? = "runecore_db",
    val username: String? = "root",
    val password: String? = "root",
    val pool: PoolConfig = PoolConfig(),
)
