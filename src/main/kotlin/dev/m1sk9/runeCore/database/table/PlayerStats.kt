package dev.m1sk9.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * プレイヤーのスタッツ情報テーブル
 * キル数やデス数を記録する
 */
object PlayerStats : Table("player_stats") {
    val uuid = uuid("uuid") references Players.uuid
    val kills = uinteger("kills").default(0u)
    val deaths = uinteger("deaths").default(0u)
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}
