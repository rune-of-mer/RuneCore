package org.lyralis.runeCore.teleport

import org.bukkit.Location
import org.bukkit.entity.Player
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.world.RuneWorldUtils
import java.util.logging.Logger

/**
 * テレポート実行結果を表すsealed class。
 */
sealed class TeleportResult {
    /**
     * テレポート成功。
     *
     * @param newBalance テレポート後の残高
     */
    data class Success(
        val newBalance: ULong,
    ) : TeleportResult()

    /**
     * 残高不足でテレポート失敗。
     *
     * @param current 現在の残高
     * @param required 必要な金額
     */
    data class InsufficientBalance(
        val current: ULong,
        val required: ULong,
    ) : TeleportResult()

    /**
     * トランザクション処理失敗。
     */
    data object TransactionFailed : TeleportResult()

    /**
     * テレポート先が見つからない。
     */
    data object LocationNotFound : TeleportResult()

    /**
     * プレイヤーが DZ にいる
     */
    data object PlayerInDZ : TeleportResult()
}

/**
 * テレポート処理を実行するサービスクラス。
 * 所持金の差し引きとテレポート実行を統合的に管理します。
 */
class TeleportService(
    private val moneyService: MoneyService,
    private val logger: Logger,
) {
    /**
     * プレイヤーをテレポートさせます。
     * 料金が0の場合は無料でテレポートします。
     *
     * @param player テレポートするプレイヤー
     * @param destination テレポート先
     * @param cost テレポート料金
     * @return テレポート結果
     */
    fun executeTeleport(
        player: Player,
        destination: Location,
        cost: ULong,
    ): TeleportResult {
        if (RuneWorldUtils.isExecute(player.world)) {
            return TeleportResult.PlayerInDZ
        }

        // 料金が0の場合は無料でテレポート
        if (cost == 0uL) {
            player.teleport(destination)
            return TeleportResult.Success(moneyService.getBalance(player.uniqueId))
        }

        // 所持金チェック
        val balance = moneyService.getBalance(player.uniqueId)
        if (balance < cost) {
            return TeleportResult.InsufficientBalance(balance, cost)
        }

        // 所持金を差し引き
        val newBalance =
            moneyService.subtractBalance(player, cost)
                ?: return TeleportResult.TransactionFailed

        // テレポート実行
        player.teleport(destination)

        logger.fine(
            "${player.name} teleported to ${destination.world?.name}:${destination.blockX},${destination.blockY},${destination.blockZ} (cost: $cost Rune)",
        )

        return TeleportResult.Success(newBalance)
    }

    /**
     * 無料でプレイヤーをテレポートさせます。
     *
     * @param player テレポートするプレイヤー
     * @param destination テレポート先
     * @return テレポート結果
     */
    fun executeFreeport(
        player: Player,
        destination: Location,
    ): TeleportResult {
        player.teleport(destination)
        return TeleportResult.Success(moneyService.getBalance(player.uniqueId))
    }
}
