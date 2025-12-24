package org.lyralis.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * プレイヤーのガチャ天井カウントテーブル
 *
 * 各プレイヤーの各イベントにおける天井カウントを管理する
 */
object PlayerGachaPity : Table("player_gacha_pity") {
    val playerUuid = uuid("player_uuid")
    val eventId = varchar("event_id", 50).references(GachaEvents.id)
    val pullCount = uinteger("pull_count").default(0u)
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(playerUuid, eventId)
}
