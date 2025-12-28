package org.lyralis.runeCore.config.model

data class TeleportConfig(
    val requestTimeoutSeconds: Long = 60,
    val defaultWarpSlots: Int = 3,
    val costs: TeleportCostConfig = TeleportCostConfig(),
)

data class TeleportCostConfig(
    val maxCost: ULong = 3000uL,
    val crossWorldBaseCost: ULong = 500uL,
    val distanceTiers: List<DistanceTier> = defaultDistanceTiers(),
    val crossWorldDistanceTiers: List<DistanceTier> = defaultCrossWorldDistanceTiers(),
) {
    companion object {
        fun defaultDistanceTiers() =
            listOf(
                DistanceTier(0.0, 100.0, 30uL),
                DistanceTier(100.0, 500.0, 150uL),
                DistanceTier(500.0, 1000.0, 300uL),
                DistanceTier(1000.0, 2000.0, 600uL),
                DistanceTier(2000.0, 5000.0, 1200uL),
                DistanceTier(5000.0, 10000.0, 1800uL),
                DistanceTier(10000.0, Double.MAX_VALUE, 2400uL),
            )

        fun defaultCrossWorldDistanceTiers() =
            listOf(
                DistanceTier(0.0, 100.0, 150uL),
                DistanceTier(100.0, 500.0, 300uL),
                DistanceTier(500.0, 1000.0, 450uL),
                DistanceTier(1000.0, 2000.0, 600uL),
                DistanceTier(2000.0, Double.MAX_VALUE, 900uL),
            )
    }
}

data class DistanceTier(
    val minDistance: Double,
    val maxDistance: Double,
    val cost: ULong,
)
