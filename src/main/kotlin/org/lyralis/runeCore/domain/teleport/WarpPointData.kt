package org.lyralis.runeCore.domain.teleport

import org.bukkit.Location
import org.bukkit.Server
import java.time.LocalDateTime
import java.util.UUID

/**
 * ワープポイントのデータモデル。
 */
data class WarpPointData(
    val id: Int,
    val ownerUuid: UUID,
    val name: String,
    val worldName: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val createdAt: LocalDateTime,
) {
    /**
     * このワープポイントをBukkitのLocationに変換します。
     *
     * @param server サーバーインスタンス
     * @return ワールドが存在する場合はLocation、存在しない場合はnull
     */
    fun toLocation(server: Server): Location? {
        val world = server.getWorld(worldName) ?: return null
        return Location(world, x, y, z, yaw, pitch)
    }

    companion object {
        /**
         * BukkitのLocationからワープポイントデータを作成します。
         *
         * @param id ワープポイントID（新規作成時は0）
         * @param ownerUuid オーナーのUUID
         * @param name ワープポイント名
         * @param location 保存する位置
         * @return WarpPointData
         */
        fun fromLocation(
            id: Int = 0,
            ownerUuid: UUID,
            name: String,
            location: Location,
        ): WarpPointData =
            WarpPointData(
                id = id,
                ownerUuid = ownerUuid,
                name = name,
                worldName = location.world?.name ?: "world",
                x = location.x,
                y = location.y,
                z = location.z,
                yaw = location.yaw,
                pitch = location.pitch,
                createdAt = LocalDateTime.now(),
            )
    }
}
