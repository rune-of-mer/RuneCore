package org.lyralis.runeCore.database.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.lyralis.runeCore.database.table.GachaEvents
import org.lyralis.runeCore.database.table.PlayerGachaPity
import org.lyralis.runeCore.domain.gacha.GachaEventData
import org.lyralis.runeCore.domain.gacha.PlayerGachaPityData
import java.time.LocalDateTime
import java.util.UUID

/**
 * ガチャ関連のデータベース操作を行うリポジトリ
 */
class GachaRepository {
    /**
     * 全てのガチャイベントを取得
     */
    fun getAllEvents(): RepositoryResult<List<GachaEventData>> =
        try {
            transaction {
                val events =
                    GachaEvents
                        .selectAll()
                        .map { row ->
                            GachaEventData(
                                id = row[GachaEvents.id],
                                displayName = row[GachaEvents.displayName],
                                ticketCost = row[GachaEvents.ticketCost],
                                isActive = row[GachaEvents.isActive],
                                pityThreshold = row[GachaEvents.pityThreshold],
                            )
                        }
                RepositoryResult.Success(events)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * アクティブなガチャイベントのみを取得
     */
    fun getActiveEvents(): RepositoryResult<List<GachaEventData>> =
        try {
            transaction {
                val events =
                    GachaEvents
                        .selectAll()
                        .where { GachaEvents.isActive eq true }
                        .map { row ->
                            GachaEventData(
                                id = row[GachaEvents.id],
                                displayName = row[GachaEvents.displayName],
                                ticketCost = row[GachaEvents.ticketCost],
                                isActive = row[GachaEvents.isActive],
                                pityThreshold = row[GachaEvents.pityThreshold],
                            )
                        }
                RepositoryResult.Success(events)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * IDでガチャイベントを取得
     */
    fun getEventById(eventId: String): RepositoryResult<GachaEventData> =
        try {
            transaction {
                val row =
                    GachaEvents
                        .selectAll()
                        .where { GachaEvents.id eq eventId }
                        .singleOrNull()

                if (row != null) {
                    RepositoryResult.Success(
                        GachaEventData(
                            id = row[GachaEvents.id],
                            displayName = row[GachaEvents.displayName],
                            ticketCost = row[GachaEvents.ticketCost],
                            isActive = row[GachaEvents.isActive],
                            pityThreshold = row[GachaEvents.pityThreshold],
                        ),
                    )
                } else {
                    RepositoryResult.NotFound("ガチャイベント '$eventId' が見つかりません")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * ガチャイベントを作成または更新
     */
    fun upsertEvent(event: GachaEventData): RepositoryResult<Unit> =
        try {
            transaction {
                val exists =
                    GachaEvents
                        .selectAll()
                        .where { GachaEvents.id eq event.id }
                        .count() > 0

                if (exists) {
                    GachaEvents.update({ GachaEvents.id eq event.id }) {
                        it[displayName] = event.displayName
                        it[ticketCost] = event.ticketCost
                        it[isActive] = event.isActive
                        it[pityThreshold] = event.pityThreshold
                        it[updatedAt] = LocalDateTime.now()
                    }
                } else {
                    GachaEvents.insert {
                        it[id] = event.id
                        it[displayName] = event.displayName
                        it[ticketCost] = event.ticketCost
                        it[isActive] = event.isActive
                        it[pityThreshold] = event.pityThreshold
                    }
                }
                RepositoryResult.Success(Unit)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * プレイヤーの天井カウントを取得
     */
    fun getPlayerPity(
        playerUuid: UUID,
        eventId: String,
    ): RepositoryResult<PlayerGachaPityData> =
        try {
            transaction {
                val row =
                    PlayerGachaPity
                        .selectAll()
                        .where {
                            (PlayerGachaPity.playerUuid eq playerUuid) and
                                (PlayerGachaPity.eventId eq eventId)
                        }.singleOrNull()

                if (row != null) {
                    RepositoryResult.Success(
                        PlayerGachaPityData(
                            playerUuid = row[PlayerGachaPity.playerUuid],
                            eventId = row[PlayerGachaPity.eventId],
                            pullCount = row[PlayerGachaPity.pullCount],
                        ),
                    )
                } else {
                    RepositoryResult.Success(
                        PlayerGachaPityData(
                            playerUuid = playerUuid,
                            eventId = eventId,
                            pullCount = 0u,
                        ),
                    )
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * プレイヤーの天井カウントを更新
     */
    fun updatePlayerPity(
        playerUuid: UUID,
        eventId: String,
        pullCount: UInt,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val exists =
                    PlayerGachaPity
                        .selectAll()
                        .where {
                            (PlayerGachaPity.playerUuid eq playerUuid) and
                                (PlayerGachaPity.eventId eq eventId)
                        }.count() > 0

                if (exists) {
                    PlayerGachaPity.update({
                        (PlayerGachaPity.playerUuid eq playerUuid) and
                            (PlayerGachaPity.eventId eq eventId)
                    }) {
                        it[this.pullCount] = pullCount
                        it[updatedAt] = LocalDateTime.now()
                    }
                } else {
                    PlayerGachaPity.insert {
                        it[this.playerUuid] = playerUuid
                        it[this.eventId] = eventId
                        it[this.pullCount] = pullCount
                    }
                }
                RepositoryResult.Success(Unit)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * プレイヤーの天井カウントをリセット
     */
    fun resetPlayerPity(
        playerUuid: UUID,
        eventId: String,
    ): RepositoryResult<Unit> = updatePlayerPity(playerUuid, eventId, 0u)

    /**
     * プレイヤーの天井カウントを増加
     */
    fun incrementPlayerPity(
        playerUuid: UUID,
        eventId: String,
        amount: UInt = 1u,
    ): RepositoryResult<UInt> =
        try {
            transaction {
                val currentPity = getPlayerPity(playerUuid, eventId).getOrNull()
                val newCount = (currentPity?.pullCount ?: 0u) + amount
                updatePlayerPity(playerUuid, eventId, newCount)
                RepositoryResult.Success(newCount)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
}
