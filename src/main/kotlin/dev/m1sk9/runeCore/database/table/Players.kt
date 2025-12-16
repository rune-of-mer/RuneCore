package dev.m1sk9.runeCore.database.table

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
    val createdAt = datetime("create_at").default(LocalDateTime.now())
    val updatedAt = datetime("update_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(uuid)
}
