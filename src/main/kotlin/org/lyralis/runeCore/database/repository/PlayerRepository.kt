package org.lyralis.runeCore.database.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.lyralis.runeCore.database.model.PlayerData
import org.lyralis.runeCore.database.table.PlayerStats
import org.lyralis.runeCore.database.table.Players
import java.time.LocalDateTime
import java.util.UUID

class PlayerRepository {
    private fun ResultRow.toPlayer() =
        PlayerData(
            uuid = this[Players.uuid],
            level = this[Players.level],
            experience = this[Players.experience],
            balance = this[Players.balance],
            createdAt = this[Players.createdAt],
            updatedAt = this[Players.updatedAt],
        )

    /**
     * プレイヤーデータを新規作成します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と [PlayerData]
     */
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

    /**
     * 指定された UUID にマッチするプレイヤーデータを検索します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と [PlayerData]
     */
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

    /**
     * 指定された UUID にマッチするプレイヤーデータが存在するかどうか確認します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と [Boolean] - 存在する場合は true, 存在しない場合は false を返します．
     */
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

    /**
     * 指定された UUID のプレイヤーのレベルを設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param newLevel 新しいレベル
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーに経験値を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加する経験値
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーの経験値を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定する経験値
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーの所持金を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と所持金
     */
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

    /**
     * 指定された UUID のプレイヤーの所持金を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定する所持金
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーに所持金を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加する所持金
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーから所持金を減算します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 減算する所持金
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤー間で所持金を送金します．
     *
     * @param fromUuid 送金元のプレイヤーの UUID
     * @param toUuid 送金先のプレイヤーの UUID
     * @param amount 送金する所持金
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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
