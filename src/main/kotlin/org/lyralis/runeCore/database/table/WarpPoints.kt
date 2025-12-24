package org.lyralis.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * ワープポイントテーブル
 * プレイヤーが登録したワープ地点を保存します。
 */
object WarpPoints : Table("warp_points") {
    val id = integer("id").autoIncrement()
    val ownerUuid = uuid("owner_uuid").references(Players.uuid)
    val name = varchar("name", 32)
    val worldName = varchar("world_name", 64)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)

    init {
        // 同一プレイヤー内で名前はユニーク
        uniqueIndex(ownerUuid, name)
    }
}
