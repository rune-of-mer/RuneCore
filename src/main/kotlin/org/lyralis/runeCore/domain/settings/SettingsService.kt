package org.lyralis.runeCore.domain.settings

import org.bukkit.entity.Player
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.database.repository.SettingsRepository
import org.lyralis.runeCore.domain.player.PlayerSettingKey
import org.lyralis.runeCore.domain.player.PlayerSettingsData
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

/**
 * プレイヤー設定を管理するサービス
 * キャッシュを使用してデータベースアクセスを最小限に抑える
 */
class SettingsService(
    private val settingsRepository: SettingsRepository,
    private val logger: Logger,
) {
    private val settingsCache = ConcurrentHashMap<UUID, PlayerSettingsData>()

    /**
     * 指定したプレイヤーの設定を取得します
     *
     * @param uuid プレイヤーの UUID
     * @return プレイヤーの設定データ
     */
    fun getSettings(uuid: UUID): PlayerSettingsData {
        settingsCache[uuid]?.let { return it }

        return when (val result = settingsRepository.findByUuid(uuid)) {
            is RepositoryResult.Success -> {
                settingsCache[uuid] = result.data
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player settings not found for $uuid")
                PlayerSettingsData(uuid)
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to load settings for $uuid: ${result.exception.message}")
                PlayerSettingsData(uuid)
            }
            else -> PlayerSettingsData(uuid)
        }
    }

    /**
     * 指定したプレイヤーの特定の設定値を取得します
     *
     * @param uuid プレイヤーの UUID
     * @param key 設定キー
     * @return 設定値
     */
    fun getSetting(
        uuid: UUID,
        key: PlayerSettingKey,
    ): Boolean {
        val settings = getSettings(uuid)
        return when (key) {
            PlayerSettingKey.SHOW_BOSS_BAR -> settings.showBossBar
        }
    }

    /**
     * 指定したプレイヤーの設定値を更新します
     *
     * @param uuid プレイヤーの UUID
     * @param key 設定キー
     * @param value 新しい設定値
     * @return 更新に成功した場合は true
     */
    fun setSetting(
        uuid: UUID,
        key: PlayerSettingKey,
        value: Boolean,
    ): Boolean =
        when (val result = settingsRepository.setSetting(uuid, key, value)) {
            is RepositoryResult.Success -> {
                updateCache(uuid, key, value)
                true
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player settings not found for $uuid")
                false
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to update setting for $uuid: ${result.exception.message}")
                false
            }
            else -> false
        }

    /**
     * 指定したプレイヤーの設定値をトグルします
     *
     * @param uuid プレイヤーの UUID
     * @param key 設定キー
     * @return 新しい設定値、失敗した場合は null
     */
    fun toggleSetting(
        uuid: UUID,
        key: PlayerSettingKey,
    ): Boolean? =
        when (val result = settingsRepository.toggleSetting(uuid, key)) {
            is RepositoryResult.Success -> {
                updateCache(uuid, key, result.data)
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player settings not found for $uuid")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to toggle setting for $uuid: ${result.exception.message}")
                null
            }
            else -> null
        }

    /**
     * プレイヤーがボスバーを表示する設定かどうかを返します
     *
     * @param player プレイヤー
     * @return ボスバーを表示する場合は true
     */
    fun shouldShowBossBar(player: Player): Boolean = getSetting(player.uniqueId, PlayerSettingKey.SHOW_BOSS_BAR)

    /**
     * プレイヤーの設定をデータベースからロードしてキャッシュに保存します
     *
     * @param uuid プレイヤーの UUID
     */
    fun loadSettings(uuid: UUID) {
        when (val result = settingsRepository.findByUuid(uuid)) {
            is RepositoryResult.Success -> {
                settingsCache[uuid] = result.data
                logger.info("Loaded settings for $uuid")
            }
            is RepositoryResult.NotFound -> {
                logger.info("Player settings not found for $uuid, using defaults")
                settingsCache[uuid] = PlayerSettingsData(uuid)
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to load settings for $uuid: ${result.exception.message}")
                settingsCache[uuid] = PlayerSettingsData(uuid)
            }
            else -> {
                logger.warning("Unexpected result when loading settings for $uuid")
                settingsCache[uuid] = PlayerSettingsData(uuid)
            }
        }
    }

    /**
     * プレイヤーのキャッシュをクリアします
     *
     * @param uuid プレイヤーの UUID
     */
    fun clearCache(uuid: UUID) {
        settingsCache.remove(uuid)
    }

    /**
     * 全てのキャッシュをクリアします
     */
    fun clearAllCache() {
        settingsCache.clear()
    }

    private fun updateCache(
        uuid: UUID,
        key: PlayerSettingKey,
        value: Boolean,
    ) {
        val current = settingsCache[uuid] ?: PlayerSettingsData(uuid)
        val updated =
            when (key) {
                PlayerSettingKey.SHOW_BOSS_BAR -> current.copy(showBossBar = value)
            }
        settingsCache[uuid] = updated
    }
}
