package dev.m1sk9.runeCore.database.repository

import dev.m1sk9.runeCore.database.model.PlayerStatsData
import dev.m1sk9.runeCore.database.table.PlayerStats
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class StatsRepository {
    private fun ResultRow.toStats() =
        PlayerStatsData(
            uuid = this[PlayerStats.uuid],
            kills = this[PlayerStats.kills],
            deaths = this[PlayerStats.deaths],
            updatedAt = this[PlayerStats.updatedAt],
        )

    fun findByUuid(uuid: String): RepositoryResult<PlayerStatsData> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it.toStats() }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun getKills(uuid: String): RepositoryResult<UInt> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it[PlayerStats.kills] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun setKills(
        uuid: String,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[kills] = amount
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player stats not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun incrementKills(uuid: String): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[kills] = kills + 1u
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player stats not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun addKills(
        uuid: String,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[kills] = kills + amount
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player stats not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun getDeaths(uuid: String): RepositoryResult<UInt> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it[PlayerStats.deaths] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun setDeaths(
        uuid: String,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[deaths] = amount
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player stats not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun incrementDeaths(uuid: String): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[deaths] = deaths + 1u
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player stats not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun addDeaths(
        uuid: String,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[deaths] = deaths + amount
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player stats not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun getKillDeathRatio(uuid: String): RepositoryResult<Double> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { row ->
                        val kills = row[PlayerStats.kills].toDouble()
                        val deaths = row[PlayerStats.deaths].toDouble()
                        if (deaths == 0.0) kills else kills / deaths
                    }.singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
}
