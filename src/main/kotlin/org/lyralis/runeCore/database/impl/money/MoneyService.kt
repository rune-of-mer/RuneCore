package org.lyralis.runeCore.database.impl.money

import org.bukkit.entity.Player
import org.lyralis.runeCore.database.repository.PlayerRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class MoneyService(
    private val playerRepository: PlayerRepository,
    private val logger: Logger,
) {
    private val balanceCache = ConcurrentHashMap<UUID, ULong>()

    /**
     * 指定したプレイヤーの所持金を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return 所持金
     */
    fun getBalance(uuid: UUID): ULong {
        balanceCache[uuid]?.let { return it }

        return when (val result = playerRepository.getBalance(uuid)) {
            is RepositoryResult.Success -> {
                balanceCache[uuid] = result.data
                result.data
            }
            is RepositoryResult.NotFound -> {
                logger.warning("No balance found for $uuid")
                0uL
            }
            is RepositoryResult.Error -> {
                logger.severe("An error occurred while fetching balance for $uuid: ${result.exception.message}")
                0uL
            }
            else -> 0uL
        }
    }

    /**
     * 指定したプレイヤーに所持金を追加します．
     *
     * @param player プレイヤー
     * @param amount 追加する金額
     * @return 追加後の所持金．失敗した場合は null
     */
    fun addBalance(
        player: Player,
        amount: ULong,
    ): ULong? {
        if (amount == 0uL) return null

        val uuid = player.uniqueId
        val currentBalance = getBalance(uuid)
        val newBalance = currentBalance + amount

        return when (val result = playerRepository.addBalance(uuid, amount)) {
            is RepositoryResult.Success -> {
                balanceCache[uuid] = newBalance
                newBalance
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player not found for ${player.name}")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to add balance to ${player.name}: ${result.exception.message}")
                null
            }
            else -> null
        }
    }

    /**
     * 指定したプレイヤーから所持金を減算します．
     *
     * @param player プレイヤー
     * @param amount 減算する金額
     * @return 減算後の所持金．失敗した場合は null
     */
    fun subtractBalance(
        player: Player,
        amount: ULong,
    ): ULong? {
        if (amount == 0uL) return null

        val uuid = player.uniqueId
        val currentBalance = getBalance(uuid)

        if (currentBalance < amount) {
            return null
        }

        val newBalance = currentBalance - amount

        return when (val result = playerRepository.subtractBalance(uuid, amount)) {
            is RepositoryResult.Success -> {
                balanceCache[uuid] = newBalance
                newBalance
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player not found for ${player.name}")
                null
            }
            is RepositoryResult.InsufficientBalance -> {
                logger.info("Insufficient balance for ${player.name}: current=${result.current}, required=${result.required}")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to subtract balance from ${player.name}: ${result.exception.message}")
                null
            }
        }
    }

    /**
     * 指定したプレイヤーの所持金を設定します．
     *
     * @param player プレイヤー
     * @param amount 設定する金額
     * @return 設定後の所持金．失敗した場合は null
     */
    fun setBalance(
        player: Player,
        amount: ULong,
    ): ULong? {
        val uuid = player.uniqueId

        return when (val result = playerRepository.setBalance(uuid, amount)) {
            is RepositoryResult.Success -> {
                balanceCache[uuid] = amount
                amount
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player not found for ${player.name}")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to set balance for ${player.name}: ${result.exception.message}")
                null
            }
            else -> null
        }
    }

    /**
     * 指定したプレイヤー同士で所持金を移動します
     *
     * @param from 移動元のプレイヤー
     * @param to 移動先のプレイヤー
     * @param amount 移動する金額
     * @return 早期処理後の所持金が [Pair] として返されます．左辺には送金元の所持金・右辺には送金先の金額．トランザクションに失敗した場合は null が返ってきます
     */
    fun transferBalance(
        from: Player,
        to: Player,
        amount: ULong,
    ): Pair<ULong, ULong>? {
        if (amount == 0uL) return null

        val fromUuid = from.uniqueId
        val toUuid = to.uniqueId

        if (fromUuid == toUuid) return null

        val currentFromBalance = getBalance(fromUuid)
        val currentToBalance = getBalance(toUuid)

        return when (val result = playerRepository.transferBalance(fromUuid, toUuid, amount)) {
            is RepositoryResult.Success -> {
                // トランザクション成功後にキャッシュを更新
                val newFromBalance = currentFromBalance - amount
                val newToBalance = currentToBalance + amount

                balanceCache[fromUuid] = newFromBalance
                balanceCache[toUuid] = newToBalance

                Pair(newFromBalance, newToBalance)
            }
            is RepositoryResult.InsufficientBalance -> {
                logger.info("Insufficient balance for ${from.name}: current=${result.current}, required=${result.required}")
                null
            }
            is RepositoryResult.NotFound -> {
                logger.warning("Player not found during transfer: ${from.name} -> ${to.name}")
                null
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to transfer balance: ${result.exception.message}")
                null
            }
        }
    }

    /**
     * 所持金キャッシュを読み込みます．
     *
     * @param uuid UUID
     */
    fun loadBalance(uuid: UUID) {
        when (val result = playerRepository.getBalance(uuid)) {
            is RepositoryResult.Success -> {
                balanceCache[uuid] = result.data
                logger.info("Loaded balance for $uuid: ${result.data}")
            }
            is RepositoryResult.NotFound -> {
                logger.info("Balance not found for $uuid, will use default")
                balanceCache[uuid] = 0uL
            }
            is RepositoryResult.Error -> {
                logger.severe("Failed to load balance for $uuid: ${result.exception.message}")
                balanceCache[uuid] = 0uL
            }
            else -> {
                balanceCache[uuid] = 0uL
            }
        }
    }

    /**
     * キャッシュを初期化します．
     *
     * @param uuid UUID
     */
    fun clearCache(uuid: UUID) {
        balanceCache.remove(uuid)
    }

    /**
     * 全てのキャッシュを初期化します．
     */
    fun clearAllCache() {
        balanceCache.clear()
    }
}
