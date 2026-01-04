package org.lyralis.runeCore.domain.money

import org.bukkit.entity.Player
import org.lyralis.runeCore.domain.player.PlayerService
import java.util.UUID
import java.util.logging.Logger

class MoneyService(
    private val playerService: PlayerService,
    private val logger: Logger,
) {
    /**
     * 指定したプレイヤーの所持金を取得します．
     *
     * @param uuid プレイヤーの UUID
     * @return 所持金
     */
    fun getBalance(uuid: UUID): ULong = playerService.getBalance(uuid)

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

        if (!playerService.addBalance(uuid, amount)) {
            logger.warning("Failed to add balance to ${player.name}")
            return null
        }

        return playerService.getBalance(uuid)
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
        val currentBalance = playerService.getBalance(uuid)

        if (currentBalance < amount) {
            return null
        }

        if (!playerService.subtractBalance(uuid, amount)) {
            logger.warning("Failed to subtract balance from ${player.name}")
            return null
        }

        return playerService.getBalance(uuid)
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

        if (!playerService.setBalance(uuid, amount)) {
            logger.warning("Failed to set balance for ${player.name}")
            return null
        }

        return amount
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

        if (!playerService.transferBalance(fromUuid, toUuid, amount)) {
            logger.warning("Failed to transfer balance: ${from.name} -> ${to.name}")
            return null
        }

        return Pair(
            playerService.getBalance(fromUuid),
            playerService.getBalance(toUuid),
        )
    }

    /**
     * 所持金キャッシュを読み込みます．
     *
     * @param uuid UUID
     */
    fun loadBalance(uuid: UUID) = playerService.loadPlayerData(uuid)

    /**
     * キャッシュを初期化します．
     *
     * @param uuid UUID
     */
    fun clearCache(uuid: UUID) = playerService.clearCache(uuid)

    /**
     * 全てのキャッシュを初期化します．
     */
    fun clearAllCache() = playerService.clearAllCache()
}
