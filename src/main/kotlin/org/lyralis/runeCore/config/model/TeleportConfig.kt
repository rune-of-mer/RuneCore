package org.lyralis.runeCore.config.model

data class TeleportConfig(
    val requestTimeoutSeconds: Long = 60,
    val defaultWarpSlots: Int = 3,
    val costs: TeleportCostConfig = TeleportCostConfig(),
)

data class TeleportCostConfig(
    val maxCost: ULong = 1000uL,
    val crossWorldBaseCost: ULong = 500uL,
    val distanceTiers: List<DistanceTier> = defaultDistanceTiers(),
    val crossWorldDistanceTiers: List<DistanceTier> = defaultCrossWorldDistanceTiers(),
) {
    companion object {
        fun defaultDistanceTiers() =
            listOf(
                DistanceTier(0.0, 100.0, 10uL),
                DistanceTier(100.0, 500.0, 50uL),
                DistanceTier(500.0, 1000.0, 100uL),
                DistanceTier(1000.0, 2000.0, 200uL),
                DistanceTier(2000.0, 5000.0, 400uL),
                DistanceTier(5000.0, 10000.0, 600uL),
                DistanceTier(10000.0, Double.MAX_VALUE, 800uL),
            )

        fun defaultCrossWorldDistanceTiers() =
            listOf(
                DistanceTier(0.0, 100.0, 50uL),
                DistanceTier(100.0, 500.0, 100uL),
                DistanceTier(500.0, 1000.0, 150uL),
                DistanceTier(1000.0, 2000.0, 200uL),
                DistanceTier(2000.0, Double.MAX_VALUE, 300uL),
            )
    }
}

data class DistanceTier(
    val minDistance: Double,
    val maxDistance: Double,
    val cost: ULong,
)
