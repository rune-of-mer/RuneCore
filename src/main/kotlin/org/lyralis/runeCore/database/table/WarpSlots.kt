package org.lyralis.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * ワープスロットテーブル
 * プレイヤーが追加で獲得したワープスロット数を管理します。
 * デフォルトのスロット数はconfig.ymlで設定され、このテーブルには追加スロット数のみ保存されます。
 */
object WarpSlots : Table("warp_slots") {
    val uuid = uuid("uuid").references(Players.uuid)
    val additionalSlots = integer("additional_slots").default(0)
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(uuid)
}
