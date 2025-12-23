package org.lyralis.runeCore.database.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.lyralis.runeCore.database.model.PlayerSettingKey
import org.lyralis.runeCore.database.model.PlayerSettingsData
import org.lyralis.runeCore.database.table.PlayerSettings
import java.time.LocalDateTime
import java.util.UUID

class SettingsRepository {
    private fun ResultRow.toSettings() =
        PlayerSettingsData(
            uuid = this[PlayerSettings.uuid],
            showBossBar = this[PlayerSettings.showBossBar],
            updatedAt = this[PlayerSettings.updatedAt],
        )

    /**
     * 指定された UUID にマッチするプレイヤーの設定データを検索します．
     *
     * @param uuid プレイヤーの UUID
     * @return データベース操作の結果を示す [RepositoryResult] と [PlayerSettingsData]
     */
    fun findByUuid(uuid: UUID): RepositoryResult<PlayerSettingsData> =
        try {
            transaction {
                PlayerSettings
                    .selectAll()
                    .where { PlayerSettings.uuid eq uuid }
                    .map { it.toSettings() }
                    .singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player settings not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * 指定された UUID のプレイヤーの設定値を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @param key 設定キー
     * @return データベース操作の結果を示す [RepositoryResult] と設定値
     */
    fun getSetting(
        uuid: UUID,
        key: PlayerSettingKey,
    ): RepositoryResult<Boolean> =
        try {
            transaction {
                PlayerSettings
                    .selectAll()
                    .where { PlayerSettings.uuid eq uuid }
                    .map { row ->
                        when (key) {
                            PlayerSettingKey.SHOW_BOSS_BAR -> row[PlayerSettings.showBossBar]
                        }
                    }.singleOrNull()
                    ?.let { RepositoryResult.Success(it) }
                    ?: RepositoryResult.NotFound("Player settings not found: $uuid")
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * 指定された UUID のプレイヤーの設定値を更新します．
     *
     * @param uuid プレイヤーの UUID
     * @param key 設定キー
     * @param value 設定値
     * @return データベース操作の結果を示す [RepositoryResult]
     */
    fun setSetting(
        uuid: UUID,
        key: PlayerSettingKey,
        value: Boolean,
    ): RepositoryResult<Unit> =
        try {
            transaction {
                val updated =
                    PlayerSettings.update({ PlayerSettings.uuid eq uuid }) {
                        when (key) {
                            PlayerSettingKey.SHOW_BOSS_BAR -> it[showBossBar] = value
                        }
                        it[updatedAt] = LocalDateTime.now()
                    }
                if (updated > 0) {
                    RepositoryResult.Success(Unit)
                } else {
                    RepositoryResult.NotFound("Player settings not found: $uuid")
                }
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }

    /**
     * 指定された UUID のプレイヤーの設定値をトグルします．
     *
     * @param uuid プレイヤーの UUID
     * @param key 設定キー
     * @return データベース操作の結果を示す [RepositoryResult] と新しい設定値
     */
    fun toggleSetting(
        uuid: UUID,
        key: PlayerSettingKey,
    ): RepositoryResult<Boolean> =
        try {
            transaction {
                val current =
                    PlayerSettings
                        .selectAll()
                        .where { PlayerSettings.uuid eq uuid }
                        .map { row ->
                            when (key) {
                                PlayerSettingKey.SHOW_BOSS_BAR -> row[PlayerSettings.showBossBar]
                            }
                        }.singleOrNull()
                        ?: return@transaction RepositoryResult.NotFound("Player settings not found: $uuid")

                val newValue = !current
                PlayerSettings.update({ PlayerSettings.uuid eq uuid }) {
                    when (key) {
                        PlayerSettingKey.SHOW_BOSS_BAR -> it[showBossBar] = newValue
                    }
                    it[updatedAt] = LocalDateTime.now()
                }
                RepositoryResult.Success(newValue)
            }
        } catch (e: Exception) {
            RepositoryResult.Error(e)
        }
}
