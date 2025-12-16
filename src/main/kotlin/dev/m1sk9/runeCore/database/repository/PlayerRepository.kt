package dev.m1sk9.runeCore.database.repository

import dev.m1sk9.runeCore.database.model.PlayerData
import dev.m1sk9.runeCore.database.table.PlayerStats
import dev.m1sk9.runeCore.database.table.Players
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class PlayerRepository {
    private fun ResultRow.toPlayer() =
        PlayerData(
            uuid = this[Players.uuid],
            level = this[Players.level],
            experience = this[Players.experience],
            balance = this[Players.balance],
            createAt = this[Players.createdAt],
            updateAt = this[Players.updatedAt],
        )

    fun createPlayer(uuid: UUID): RepositoryResult<PlayerData> =
        try {
            transaction {
                Players.insert {
                    it[Players.uuid] = uuid
                }

                PlayerStats.insert {
                    it[PlayerStats.uuid] = uuid
                }
            }

            RepositoryResult.Success(PlayerData(uuid))
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun findByUUID(uuid: UUID): RepositoryResult<PlayerData> =
        try {
            transaction {
                Players
                    .selectAll()
                    .where { Players.uuid eq uuid }
                    .map { it.toPlayer() }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player $uuid not found")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun existsByUUID(uuid: UUID): RepositoryResult<Boolean> =
        try {
            transaction {
                val exists =
                    Players
                        .selectAll()
                        .where { Players.uuid eq uuid }
                        .count() > 0
                RepositoryResult.Success(exists)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun setLevel(
        uuid: UUID,
        newLevel: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    Players.update({ Players.uuid eq uuid }) {
                        it[level] = newLevel
                        it[updatedAt] = LocalDateTime.now()
                    }

                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player $uuid not found")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun addExperience(
        uuid: UUID,
        amount: ULong,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    Players.update({ Players.uuid eq uuid }) {
                        it[experience] = experience + amount
                        it[updatedAt] = LocalDateTime.now()
                    }

                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player $uuid not found")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun setExperience(
        uuid: UUID,
        amount: ULong,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    Players.update({ Players.uuid eq uuid }) {
                        it[experience] = amount
                        it[updatedAt] = LocalDateTime.now()
                    }

                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player $uuid not found")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun getBalance(uuid: UUID): RepositoryResult<ULong> =
        try {
            transaction {
                Players
                    .selectAll()
                    .where { Players.uuid eq uuid }
                    .map { it[Players.balance] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player $uuid not found")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun setBalance(
        uuid: UUID,
        amount: ULong,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    Players.update({ Players.uuid eq uuid }) {
                        it[balance] = amount
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun addBalance(
        uuid: UUID,
        amount: ULong,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    Players.update({ Players.uuid eq uuid }) {
                        it[balance] = balance + amount
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun subtractBalance(
        uuid: UUID,
        amount: ULong,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val current =
                    Players
                        .selectAll()
                        .where { Players.uuid eq uuid }
                        .map { it[Players.balance] }
                        .singleOrNull()
                        ?: return@transaction RepositoryResult.NotFound("Player not found: $uuid")

                if (current < amount) {
                    return@transaction RepositoryResult.InsufficientBalance(current, amount)
                }

                Players.update({ Players.uuid eq uuid }) {
                    it[balance] = balance - amount
                    it[updatedAt] = LocalDateTime.now()
                }
                RepositoryResult.Success(Unit)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    fun transferBalance(
        fromUuid: UUID,
        toUuid: UUID,
        amount: ULong,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val fromBalance =
                    Players
                        .selectAll()
                        .where { Players.uuid eq fromUuid }
                        .map { it[Players.balance] }
                        .singleOrNull()
                        ?: return@transaction RepositoryResult.NotFound("Sender not found: $fromUuid")

                val toExists =
                    Players
                        .selectAll()
                        .where { Players.uuid eq toUuid }
                        .count() > 0
                if (!toExists) {
                    return@transaction RepositoryResult.NotFound("Recipient not found: $toUuid")
                }

                if (fromBalance < amount) {
                    return@transaction RepositoryResult.InsufficientBalance(fromBalance, amount)
                }

                Players.update({ Players.uuid eq fromUuid }) {
                    it[balance] = balance - amount
                    it[updatedAt] = LocalDateTime.now()
                }

                Players.update({ Players.uuid eq toUuid }) {
                    it[balance] = balance + amount
                    it[updatedAt] = LocalDateTime.now()
                }

                RepositoryResult.Success(Unit)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
}
