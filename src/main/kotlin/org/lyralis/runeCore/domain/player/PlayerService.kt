package org.lyralis.runeCore.domain.player

import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.database.repository.StatsRepository
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class PlayerService(
    private val playerRepository: PlayerRepository,
    private val statsRepository: StatsRepository,
    private val logger: Logger,
) {
    private val playerDataCache = ConcurrentHashMap<UUID, PlayerData>()
    private val statsDataCache = ConcurrentHashMap<UUID, PlayerStatsData>()

    /**
     * プレイヤーデータを新規作成します．
     *
     * @param uuid プレイヤーの UUID
     * @return 作成に成功した場合は true，失敗した場合は false
     */
    fun createPlayer(uuid: UUID): Boolean =
        when (val result = playerRepository.createPlayer(uuid)) {
            is RepositoryResult.Success -> {
                logger.info("Created new player data: $uuid")
                true
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to create player data: $uuid - ${result.exception.message}")
                false
            }
            else -> false
        }

    /**
     * 指定された UUID のプレイヤーデータが存在するかどうか確認します．
     *
     * @param uuid プレイヤーの UUID
     * @return 存在する場合は true，存在しない場合は false
     */
    fun existsPlayer(uuid: UUID): Boolean =
        when (val result = playerRepository.existsByUUID(uuid)) {
            is RepositoryResult.Success -> result.data
            is RepositoryResult.Error -> {
                logger.severe("Failed to check player existence: $uuid - ${result.exception.message}")
                false
            }
            else -> false
        }

    /**
     * 指定された UUID のプレイヤーデータを取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return プレイヤーデータ．存在しない場合は null
     */
    fun getPlayerData(uuid: UUID): PlayerData? {
        playerDataCache[uuid]?.let { return it }

        return when (val result = playerRepository.findByUUID(uuid)) {
            is RepositoryResult.Success -> {
                playerDataCache[uuid] = result.data
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player data not found: $uuid")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to get player data: $uuid - ${result.exception.message}")
                null
            }
            else -> null
        }
    }

    /**
     * 指定された UUID のプレイヤーのレベルを取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return プレイヤーのレベル．存在しない場合は 1
     */
    fun getLevel(uuid: UUID): UInt {
        playerDataCache[uuid]?.let { return it.level }

        return when (val result = playerRepository.getLevel(uuid)) {
            is RepositoryResult.Success -> {
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player level not found: $uuid")
                1u
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to get player level: $uuid - ${result.exception.message}")
                1u
            }
            else -> 1u
        }
    }

    /**
     * 指定された UUID のプレイヤーのレベルを設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param level 設定するレベル
     * @return 設定に成功した場合は true，失敗した場合は false
     */
    fun setLevel(
        uuid: UUID,
        level: UInt,
    ): Boolean {
        val result = playerRepository.setLevel(uuid, level)
        if (result is RepositoryResult.Success) {
            playerDataCache[uuid]?.let {
                playerDataCache[uuid] = it.copy(level = level)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to set player level: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーの経験値を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return プレイヤーの経験値．存在しない場合は 0
     */
    fun getExperience(uuid: UUID): ULong {
        playerDataCache[uuid]?.let { return it.experience }

        return when (val result = playerRepository.getExperience(uuid)) {
            is RepositoryResult.Success -> {
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player experience not found: $uuid")
                0uL
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to get player experience: $uuid - ${result.exception.message}")
                0uL
            }
            else -> 0uL
        }
    }

    /**
     * 指定された UUID のプレイヤーに経験値を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加する経験値
     * @return 追加に成功した場合は true，失敗した場合は false
     */
    fun addExperience(
        uuid: UUID,
        amount: ULong,
    ): Boolean {
        val result = playerRepository.addExperience(uuid, amount)
        if (result is RepositoryResult.Success) {
            playerDataCache[uuid]?.let {
                playerDataCache[uuid] = it.copy(experience = it.experience + amount)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to add player experience: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーの経験値を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定する経験値
     * @return 設定に成功した場合は true，失敗した場合は false
     */
    fun setExperience(
        uuid: UUID,
        amount: ULong,
    ): Boolean {
        val result = playerRepository.setExperience(uuid, amount)
        if (result is RepositoryResult.Success) {
            playerDataCache[uuid]?.let {
                playerDataCache[uuid] = it.copy(experience = amount)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to set player experience: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーの所持金を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return プレイヤーの所持金．存在しない場合は 0
     */
    fun getBalance(uuid: UUID): ULong {
        playerDataCache[uuid]?.let { return it.balance }

        return when (val result = playerRepository.getBalance(uuid)) {
            is RepositoryResult.Success -> {
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player balance not found: $uuid")
                0uL
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to get player balance: $uuid - ${result.exception.message}")
                0uL
            }
            else -> 0uL
        }
    }

    /**
     * 指定された UUID のプレイヤーの所持金を設定します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 設定する所持金
     * @return 設定に成功した場合は true，失敗した場合は false
     */
    fun setBalance(
        uuid: UUID,
        amount: ULong,
    ): Boolean {
        val result = playerRepository.setBalance(uuid, amount)
        if (result is RepositoryResult.Success) {
            playerDataCache[uuid]?.let {
                playerDataCache[uuid] = it.copy(balance = amount)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to set player balance: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーに所持金を追加します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 追加する所持金
     * @return 追加に成功した場合は true，失敗した場合は false
     */
    fun addBalance(
        uuid: UUID,
        amount: ULong,
    ): Boolean {
        val result = playerRepository.addBalance(uuid, amount)
        if (result is RepositoryResult.Success) {
            playerDataCache[uuid]?.let {
                playerDataCache[uuid] = it.copy(balance = it.balance + amount)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to add player balance: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーから所持金を減算します．
     *
     * @param uuid プレイヤーの UUID
     * @param amount 減算する所持金
     * @return 減算に成功した場合は true，失敗した場合は false
     */
    fun subtractBalance(
        uuid: UUID,
        amount: ULong,
    ): Boolean {
        val result = playerRepository.subtractBalance(uuid, amount)
        if (result is RepositoryResult.Success) {
            playerDataCache[uuid]?.let {
                playerDataCache[uuid] = it.copy(balance = it.balance - amount)
            }
            return true
        }

        when (result) {
            is RepositoryResult.InsufficientBalance -> {
                logger.info("Insufficient balance for $uuid: current=${result.current}, required=${result.required}")
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to subtract player balance: $uuid - ${result.exception.message}")
            }
            else -> {}
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤー間で所持金を送金します．
     *
     * @param fromUuid 送金元のプレイヤーの UUID
     * @param toUuid 送金先のプレイヤーの UUID
     * @param amount 送金する所持金
     * @return 送金に成功した場合は true，失敗した場合は false
     */
    fun transferBalance(
        fromUuid: UUID,
        toUuid: UUID,
        amount: ULong,
    ): Boolean {
        val result = playerRepository.transferBalance(fromUuid, toUuid, amount)
        if (result is RepositoryResult.Success) {
            playerDataCache[fromUuid]?.let {
                playerDataCache[fromUuid] = it.copy(balance = it.balance - amount)
            }
            playerDataCache[toUuid]?.let {
                playerDataCache[toUuid] = it.copy(balance = it.balance + amount)
            }
            return true
        }

        when (result) {
            is RepositoryResult.InsufficientBalance -> {
                logger.info("Insufficient balance for transfer from $fromUuid: current=${result.current}, required=${result.required}")
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to transfer balance: $fromUuid -> $toUuid - ${result.exception.message}")
            }
            else -> {}
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーの最終ログイン日時を更新します．
     *
     * @param uuid プレイヤーの UUID
     * @return 更新に成功した場合は true，失敗した場合は false
     */
    fun updateLastLogin(uuid: UUID): Boolean {
        val result = playerRepository.updateLastLoginAt(uuid)
        if (result is RepositoryResult.Success) {
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to update last login: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーの最終ログイン日時を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return 最終ログイン日時．存在しない場合は null
     */
    fun getLastLogin(uuid: UUID): LocalDateTime? =
        when (val result = playerRepository.getLastLoginAt(uuid)) {
            is RepositoryResult.Success -> result.data
            is RepositoryResult.Error -> {
                logger.severe("Failed to get last login: $uuid - ${result.exception.message}")
                null
            }
            else -> null
        }

    /**
     * 指定された UUID のプレイヤーの統計データを取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return 統計データ．存在しない場合は null
     */
    fun getStats(uuid: UUID): PlayerStatsData? {
        statsDataCache[uuid]?.let { return it }

        return when (val result = statsRepository.findByUuid(uuid)) {
            is RepositoryResult.Success -> {
                statsDataCache[uuid] = result.data
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player stats not found: $uuid")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to get player stats: $uuid - ${result.exception.message}")
                null
            }
            else -> null
        }
    }

    /**
     * 指定された UUID のプレイヤーのキル数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return 増加に成功した場合は true，失敗した場合は false
     */
    fun incrementKills(uuid: UUID): Boolean {
        val result = statsRepository.incrementKills(uuid)
        if (result is RepositoryResult.Success) {
            statsDataCache[uuid]?.let {
                statsDataCache[uuid] = it.copy(kills = it.kills + 1u)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to increment kills: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーのモブキル数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return 増加に成功した場合は true，失敗した場合は false
     */
    fun incrementMobKills(uuid: UUID): Boolean {
        val result = statsRepository.incrementMobKills(uuid)
        if (result is RepositoryResult.Success) {
            statsDataCache[uuid]?.let {
                statsDataCache[uuid] = it.copy(mobKills = it.mobKills + 1u)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to increment mob kills: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーのデス数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return 増加に成功した場合は true，失敗した場合は false
     */
    fun incrementDeaths(uuid: UUID): Boolean {
        val result = statsRepository.incrementDeaths(uuid)
        if (result is RepositoryResult.Success) {
            statsDataCache[uuid]?.let {
                statsDataCache[uuid] = it.copy(deaths = it.deaths + 1u)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to increment deaths: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーのブロック破壊数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return 増加に成功した場合は true，失敗した場合は false
     */
    fun incrementBlocksDestroyed(uuid: UUID): Boolean {
        val result = statsRepository.incrementBlocksDestroys(uuid)
        if (result is RepositoryResult.Success) {
            statsDataCache[uuid]?.let {
                statsDataCache[uuid] = it.copy(blocksDestroys = it.blocksDestroys + 1u)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to increment blocks destroyed: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーのブロック設置数を1増加させます．
     *
     * @param uuid プレイヤーの UUID
     * @return 増加に成功した場合は true，失敗した場合は false
     */
    fun incrementBlocksPlaced(uuid: UUID): Boolean {
        val result = statsRepository.incrementBlocksPlaces(uuid)
        if (result is RepositoryResult.Success) {
            statsDataCache[uuid]?.let {
                statsDataCache[uuid] = it.copy(blocksPlaces = it.blocksPlaces + 1u)
            }
            return true
        }

        if (result is RepositoryResult.Error) {
            logger.severe("Failed to increment blocks placed: $uuid - ${result.exception.message}")
        }
        return false
    }

    /**
     * 指定された UUID のプレイヤーのキルデス比を取得します．
     *
     * デス数が0の場合はキル数をそのまま返します．
     *
     * @param uuid プレイヤーの UUID
     * @return キルデス比．存在しない場合は null
     */
    fun getKillDeathRatio(uuid: UUID): Double? =
        when (val result = statsRepository.getKillDeathRatio(uuid)) {
            is RepositoryResult.Success -> result.data
            is RepositoryResult.NotFound -> {
                logger.warning("Player stats not found for K/D ratio: $uuid")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to get K/D ratio: $uuid - ${result.exception.message}")
                null
            }
            else -> null
        }

    /**
     * 指定された UUID のプレイヤーデータをキャッシュにロードします．
     *
     * @param uuid プレイヤーの UUID
     */
    fun loadPlayerData(uuid: UUID) {
        when (val result = playerRepository.findByUUID(uuid)) {
            is RepositoryResult.Success -> {
                playerDataCache[uuid] = result.data
                logger.info("Loaded player data for $uuid")
            }
            is RepositoryResult.NotFound -> {
                logger.info("Player data not found for $uuid, will be created on first write")
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to load player data for $uuid: ${result.exception.message}")
            }
            else -> {}
        }
    }

    /**
     * 指定された UUID のプレイヤー統計データをキャッシュにロードします．
     *
     * @param uuid プレイヤーの UUID
     */
    fun loadStats(uuid: UUID) {
        when (val result = statsRepository.findByUuid(uuid)) {
            is RepositoryResult.Success -> {
                statsDataCache[uuid] = result.data
                logger.info("Loaded player stats for $uuid")
            }
            is RepositoryResult.NotFound -> {
                logger.info("Player stats not found for $uuid")
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to load player stats for $uuid: ${result.exception.message}")
            }
            else -> {}
        }
    }

    /**
     * 指定された UUID のプレイヤーのキャッシュをクリアします．
     *
     * @param uuid プレイヤーの UUID
     */
    fun clearCache(uuid: UUID) {
        playerDataCache.remove(uuid)
        statsDataCache.remove(uuid)
    }

    /**
     * 全てのキャッシュをクリアします．
     */
    fun clearAllCache() {
        playerDataCache.clear()
        statsDataCache.clear()
    }
}
