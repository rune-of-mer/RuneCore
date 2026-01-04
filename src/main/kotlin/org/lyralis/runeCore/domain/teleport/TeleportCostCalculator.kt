package org.lyralis.runeCore.domain.teleport

import org.bukkit.Location
import org.lyralis.runeCore.config.model.DistanceTier
import org.lyralis.runeCore.config.model.TeleportCostConfig

class TeleportCostCalculator(
    private val config: TeleportCostConfig,
) {
    /**
     * 2地点間のテレポート料金を計算します。
     *
     * @param from テレポート元の位置
     * @param to テレポート先の位置
     * @return 必要な料金（上限はconfigのmaxCost）
     */
    fun calculateCost(
        from: Location,
        to: Location,
    ): ULong {
        val isDifferentWorld = from.world?.name != to.world?.name
        val distance =
            if (isDifferentWorld) {
                // 異なるワールドの場合は座標ベクトルの距離を計算
                from.toVector().distance(to.toVector())
            } else {
                from.distance(to)
            }

        val baseCost =
            if (isDifferentWorld) {
                config.crossWorldBaseCost + findDistanceCost(distance, config.crossWorldDistanceTiers)
            } else {
                findDistanceCost(distance, config.distanceTiers)
            }

        return baseCost.coerceAtMost(config.maxCost)
    }

    /**
     * 距離から料金を算出します（ワールド間移動かどうかは呼び出し元で判断）。
     *
     * @param distance ブロック単位の距離
     * @param isDifferentWorld 異なるワールド間の移動か
     * @return 必要な料金（上限はconfigのmaxCost）
     */
    fun calculateCost(
        distance: Double,
        isDifferentWorld: Boolean,
    ): ULong {
        val baseCost =
            if (isDifferentWorld) {
                config.crossWorldBaseCost + findDistanceCost(distance, config.crossWorldDistanceTiers)
            } else {
                findDistanceCost(distance, config.distanceTiers)
            }

        return baseCost.coerceAtMost(config.maxCost)
    }

    private fun findDistanceCost(
        distance: Double,
        tiers: List<DistanceTier>,
    ): ULong = tiers.find { distance >= it.minDistance && distance < it.maxDistance }?.cost ?: 0uL
}
