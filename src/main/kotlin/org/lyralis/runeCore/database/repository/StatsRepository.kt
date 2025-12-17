package org.lyralis.runeCore.database.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.lyralis.runeCore.database.model.PlayerStatsData
import org.lyralis.runeCore.database.table.PlayerStats
import java.time.LocalDateTime
import java.util.UUID

class StatsRepository {
    private fun ResultRow.toStats() =
        PlayerStatsData(
            uuid = this[PlayerStats.uuid],
            kills = this[PlayerStats.kills],
            deaths = this[PlayerStats.deaths],
            mobKills = this[PlayerStats.mobKills],
            updatedAt = this[PlayerStats.updatedAt],
        )

    /**
     * 指定された UUID にマッチするプレイヤーの統計データを検索します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と [PlayerStatsData]
     */
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

    /**
     * 指定された UUID のプレイヤーのキル数を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] とキル数
     */
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

    /**
     * 指定された UUID のプレイヤーのキル数を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定するキル数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーのキル数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーにキル数を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加するキル数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーのモブキル数を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定するモブキル数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun setMobKills(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[mobKills] = amount
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

    /**
     * 指定された UUID のプレイヤーのモブキル数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun incrementMobKills(uuid: UUID): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[mobKills] = mobKills + 1u
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

    /**
     * 指定された UUID のプレイヤーにモブキル数を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加するモブキル数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun addMobKills(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[mobKills] = mobKills + amount
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

    /**
     * 指定された UUID のプレイヤーのデス数を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] とデス数
     */
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

    /**
     * 指定された UUID のプレイヤーのデス数を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定するデス数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーのデス数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーにデス数を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加するデス数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
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

    /**
     * 指定された UUID のプレイヤーのキルデス比を計算して取得します．
     *
     * デス数が0の場合はキル数をそのまま返します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] とキルデス比
     */
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
