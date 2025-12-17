package org.lyralis.runeCore.config.model

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val name: String,
    val username: String,
    val password: String,
    val pool: PoolConfig,
)
