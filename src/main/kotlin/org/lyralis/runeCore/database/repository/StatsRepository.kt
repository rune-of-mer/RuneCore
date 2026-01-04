package org.lyralis.runeCore.database.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.lyralis.runeCore.database.table.PlayerStats
import org.lyralis.runeCore.domain.player.PlayerStatsData
import java.time.LocalDateTime
import java.util.UUID

class StatsRepository {
    private fun ResultRow.toStats() =
        PlayerStatsData(
            uuid = this[PlayerStats.uuid],
            kills = this[PlayerStats.kills],
            deaths = this[PlayerStats.deaths],
            mobKills = this[PlayerStats.mobKills],
            blocksDestroys = this[PlayerStats.blocksDestroys],
            blocksPlaces = this[PlayerStats.blocksPlaces],
            loginDays = this[PlayerStats.loginDays],
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
     * 指定された UUID のプレイヤーのモブキル数を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] とモブキル数
     */
    fun getMobKills(uuid: UUID): RepositoryResult<UInt> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it[PlayerStats.mobKills] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
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
     * 指定された UUID のプレイヤーのブロック破壊数を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] とブロック破壊数
     */
    fun getBlocksDestroys(uuid: UUID): RepositoryResult<UInt> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it[PlayerStats.blocksDestroys] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * 指定された UUID のプレイヤーのブロック破壊数を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定するブロック破壊数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun setBlocksDestroys(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[blocksDestroys] = amount
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
     * 指定された UUID のプレイヤーのブロック破壊数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun incrementBlocksDestroys(uuid: UUID): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[blocksDestroys] = blocksDestroys + 1u
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
     * 指定された UUID のプレイヤーにブロック破壊数を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加するブロック破壊数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun addBlocksDestroys(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[blocksDestroys] = blocksDestroys + amount
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
     * 指定された UUID のプレイヤーのブロック設置数を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] とブロック設置数
     */
    fun getBlocksPlaces(uuid: UUID): RepositoryResult<UInt> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it[PlayerStats.blocksPlaces] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * 指定された UUID のプレイヤーのブロック設置数を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定するブロック設置数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun setBlocksPlaces(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[blocksPlaces] = amount
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
     * 指定された UUID のプレイヤーのブロック設置数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun incrementBlocksPlaces(uuid: UUID): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[blocksPlaces] = blocksPlaces + 1u
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
     * 指定された UUID のプレイヤーにブロック設置数を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加するブロック設置数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun addBlocksPlaces(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[blocksPlaces] = blocksPlaces + amount
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
     * 指定された UUID のプレイヤーの累計ログイン日数を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と累計ログイン日数
     */
    fun getLoginDays(uuid: UUID): RepositoryResult<UInt> =
        try {
            transaction {
                PlayerStats
                    .selectAll()
                    .where { PlayerStats.uuid eq uuid }
                    .map { it[PlayerStats.loginDays] }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * 指定された UUID のプレイヤーの累計ログイン日数を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定する累計ログイン日数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun setLoginDays(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[loginDays] = amount
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
     * 指定された UUID のプレイヤーの累計ログイン日数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun incrementLoginDays(uuid: UUID): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[loginDays] = loginDays + 1u
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
     * 指定された UUID のプレイヤーに累計ログイン日数を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加する累計ログイン日数
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun addLoginDays(
        uuid: UUID,
        amount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerStats.update({ PlayerStats.uuid eq uuid }) {
                        it[loginDays] = loginDays + amount
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
                        if (deaths == 0.0) kills else (kills / deaths)
                    }.singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player stats not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
}
