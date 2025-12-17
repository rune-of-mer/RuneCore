package org.lyralis.runeCore.config.model

data class PoolConfig(
    val maximumSize: Int,
    val minimumIdle: Int,
    val idleTimeout: Long,
    val connectionTimeout: Long,
)
