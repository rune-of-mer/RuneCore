package dev.m1sk9.runeCore.database.repository

import dev.m1sk9.runeCore.database.model.PlayerStatsData
import dev.m1sk9.runeCore.database.table.PlayerStats
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class StatsRepository {
    private fun ResultRow.toStats() =
        PlayerStatsData(
            uuid = this[PlayerStats.uuid],
            kills = this[PlayerStats.kills],
            deaths = this[PlayerStats.deaths],
            updatedAt = this[PlayerStats.updatedAt],
        )

    fun findByUuid(uuid: UUID): RepositoryResult<PlayerStatsData> =
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

    fun getKills(uuid: UUID): RepositoryResult<UInt> =
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
        uuid: UUID,
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

    fun incrementKills(uuid: UUID): RepositoryResult<Unit> =
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
        uuid: UUID,
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

    fun getDeaths(uuid: UUID): RepositoryResult<UInt> =
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
        uuid: UUID,
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

    fun incrementDeaths(uuid: UUID): RepositoryResult<Unit> =
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
        uuid: UUID,
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

    fun getKillDeathRatio(uuid: UUID): RepositoryResult<Double> =
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
