package dev.m1sk9.runeCore.config.model

data class PoolConfig(
    val maximumSize: Int = 10,
    val minimumIdle: Int = 2,
    val idleTimeout: Long = 60000,
    val connectionTimeout: Long = 30000,
)
