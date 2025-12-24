package org.lyralis.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * ガチャイベントテーブル
 *
 * 各ガチャイベントの設定を格納する
 */
object GachaEvents : Table("gacha_events") {
    val id = varchar("id", 50)
    val displayName = varchar("display_name", 100)
    val ticketCost = uinteger("ticket_cost").default(1u)
    val isActive = bool("is_active").default(true)
    val pityThreshold = uinteger("pity_threshold").default(100u)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}
