package org.lyralis.runeCore.database.repository

import org.bukkit.Location
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.lyralis.runeCore.database.table.WarpPoints
import org.lyralis.runeCore.database.table.WarpSlots
import org.lyralis.runeCore.domain.teleport.WarpPointData
import java.time.LocalDateTime
import java.util.UUID

/**
 * ワープポイントのリポジトリ。
 * データベースへのCRUD操作を提供します。
 */
class WarpPointRepository {
    private fun ResultRow.toWarpPointData() =
        WarpPointData(
            id = this[WarpPoints.id],
            ownerUuid = this[WarpPoints.ownerUuid],
            name = this[WarpPoints.name],
            worldName = this[WarpPoints.worldName],
            x = this[WarpPoints.x],
            y = this[WarpPoints.y],
            z = this[WarpPoints.z],
            yaw = this[WarpPoints.yaw],
            pitch = this[WarpPoints.pitch],
            createdAt = this[WarpPoints.createdAt],
        )

    /**
     * ワープポイントを作成します。
     *
     * @param ownerUuid オーナーのUUID
     * @param name ワープポイント名
     * @param location 保存する位置
     * @return 作成されたワープポイント
     */
    fun createWarpPoint(
        ownerUuid: UUID,
        name: String,
        location: Location,
    ): RepositoryResult<WarpPointData> =
        try {
            transaction {
                val id =
                    WarpPoints.insert {
                        it[WarpPoints.ownerUuid] = ownerUuid
                        it[WarpPoints.name] = name
                        it[worldName] = location.world?.name ?: "world"
                        it[x] = location.x
                        it[y] = location.y
                        it[z] = location.z
                        it[yaw] = location.yaw
                        it[pitch] = location.pitch
                    } get WarpPoints.id

                RepositoryResult.Success(
                    WarpPointData.fromLocation(id, ownerUuid, name, location),
                )
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * オーナーと名前でワープポイントを検索します。
     *
     * @param ownerUuid オーナーのUUID
     * @param name ワープポイント名
     * @return ワープポイント
     */
    fun findByOwnerAndName(
        ownerUuid: UUID,
        name: String,
    ): RepositoryResult<WarpPointData> =
        try {
            transaction {
                WarpPoints
                    .selectAll()
                    .where { (WarpPoints.ownerUuid eq ownerUuid) and (WarpPoints.name eq name) }
                    .map { it.toWarpPointData() }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("ワープポイント '$name' が見つかりません")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * オーナーの全ワープポイントを取得します。
     *
     * @param ownerUuid オーナーのUUID
     * @return ワープポイントのリスト
     */
    fun findAllByOwner(ownerUuid: UUID): RepositoryResult<List<WarpPointData>> =
        try {
            transaction {
                val warpPoints =
                    WarpPoints
                        .selectAll()
                        .where { WarpPoints.ownerUuid eq ownerUuid }
                        .map { it.toWarpPointData() }
                RepositoryResult.Success(warpPoints)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * ワープポイントを削除します。
     *
     * @param ownerUuid オーナーのUUID
     * @param name ワープポイント名
     * @return 削除成功時はUnit
     */
    fun deleteByOwnerAndName(
        ownerUuid: UUID,
        name: String,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val deleted =
                    WarpPoints.deleteWhere {
                        (WarpPoints.ownerUuid eq ownerUuid) and (WarpPoints.name eq name)
                    }
                if (deleted > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("ワープポイント '$name' が見つかりません")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * オーナーのワープポイント数をカウントします。
     *
     * @param ownerUuid オーナーのUUID
     * @return ワープポイント数
     */
    fun countByOwner(ownerUuid: UUID): RepositoryResult<Int> =
        try {
            transaction {
                val count =
                    WarpPoints
                        .selectAll()
                        .where { WarpPoints.ownerUuid eq ownerUuid }
                        .count()
                        .toInt()
                RepositoryResult.Success(count)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * プレイヤーの追加スロット数を取得します。
     *
     * @param uuid プレイヤーのUUID
     * @return 追加スロット数
     */
    fun getAdditionalSlots(uuid: UUID): RepositoryResult<Int> =
        try {
            transaction {
                val slots =
                    WarpSlots
                        .selectAll()
                        .where { WarpSlots.uuid eq uuid }
                        .map { it[WarpSlots.additionalSlots] }
                        .singleOrNull()
                        ?: 0
                RepositoryResult.Success(slots)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * プレイヤーにスロットを追加します。
     *
     * @param uuid プレイヤーのUUID
     * @param amount 追加するスロット数
     * @return 更新後の追加スロット数
     */
    fun addSlots(
        uuid: UUID,
        amount: Int,
    ): RepositoryResult<Int> =
        try {
            transaction {
                val existing =
                    WarpSlots
                        .selectAll()
                        .where { WarpSlots.uuid eq uuid }
                        .singleOrNull()

                val newTotal =
                    if (existing != null) {
                        val current = existing[WarpSlots.additionalSlots]
                        val newAmount = current + amount
                        WarpSlots.update({ WarpSlots.uuid eq uuid }) {
                            it[additionalSlots] = newAmount
                            it[updatedAt] = LocalDateTime.now()
                        }
                        newAmount
                    } else {
                        WarpSlots.insert {
                            it[WarpSlots.uuid] = uuid
                            it[additionalSlots] = amount
                        }
                        amount
                    }
                RepositoryResult.Success(newTotal)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * プレイヤーの合計スロット数を取得します（デフォルト + 追加）。
     *
     * @param uuid プレイヤーのUUID
     * @param defaultSlots デフォルトのスロット数
     * @return 合計スロット数
     */
    fun getTotalSlots(
        uuid: UUID,
        defaultSlots: Int,
    ): RepositoryResult<Int> =
        when (val result = getAdditionalSlots(uuid)) {
            is RepositoryResult.Success -> RepositoryResult.Success(defaultSlots + result.data)
            is RepositoryResult.NotFound -> RepositoryResult.Success(defaultSlots)
            is RepositoryResult.Error -> result
            is RepositoryResult.InsufficientBalance -> RepositoryResult.Success(defaultSlots)
        }

    /**
     * ワープポイントが存在するかチェックします。
     *
     * @param ownerUuid オーナーのUUID
     * @param name ワープポイント名
     * @return 存在する場合はtrue
     */
    fun exists(
        ownerUuid: UUID,
        name: String,
    ): Boolean =
        try {
            transaction {
                WarpPoints
                    .selectAll()
                    .where { (WarpPoints.ownerUuid eq ownerUuid) and (WarpPoints.name eq name) }
                    .count() > 0
            }
        } catch (e: Exception) {
            false
        }
}
