package org.lyralis.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * プレイヤーデータの基本テーブル
 * レベルや経験値，主キーとなる UUID が格納される．
 */
object Players : Table("players") {
    val uuid = uuid("uuid")
    val level = uinteger("level").default(1u)
    val experience = ulong("experience").default(0uL)
    val balance = ulong("balance").default(0uL)
    val lastLoginAt = datetime("last_login_at").default(LocalDateTime.now())
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(uuid)
}
