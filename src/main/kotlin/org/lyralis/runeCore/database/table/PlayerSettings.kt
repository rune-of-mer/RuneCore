package org.lyralis.runeCore.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * プレイヤーの個人設定テーブル
 * ボスバーの表示/非表示などのプレイヤー個別設定を保存する
 */
object PlayerSettings : Table("player_settings") {
    val uuid = uuid("uuid") references Players.uuid
    val showBossBar = bool("show_boss_bar").default(true)
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(uuid)
}
