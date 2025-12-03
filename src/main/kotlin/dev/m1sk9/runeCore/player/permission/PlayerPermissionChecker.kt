package dev.m1sk9.runeCore.player.permission

import org.bukkit.entity.Player

class RequirePermissionException(
    playerID: String,
) : Exception("権限が不足しています: $playerID")

/**
 * プレイヤーの権限をチェックするユーティリティオブジェクト．
 */
object PlayerPermissionChecker {
    /**
     * 指定されたいずれかの権限をプレイヤーが持っているかを確認します．
     *
     * @param player チェック対象のプレイヤー
     * @param permission チェックする権限の可変長引数リスト
     * @return 指定された権限のいずれかをプレイヤーが持っている場合に true を返します．
     */
    fun hasAny(
        player: Player,
        vararg permission: PlayerPermission,
    ): Boolean = permission.any { it.has(player) }

    /**
     * 指定されたすべての権限をプレイヤーが持っているかを確認します．
     *
     * @param player チェック対象のプレイヤー
     * @param permission チェックする権限の可変長引数リスト
     * @return 指定されたすべての権限をプレイヤーが持っている場合に true を返します．
     */
    fun hasAll(
        player: Player,
        vararg permission: PlayerPermission,
    ): Boolean = permission.all { it.has(player) }

    /**
     * 指定された権限をプレイヤーが持っていることを要求します．
     * プレイヤーが権限を持っていない場合、RequirePermissionException をスローします．
     *
     * @param player チェック対象のプレイヤー
     * @param permission チェックする権限
     * @throws RequirePermissionException プレイヤーが指定された権限を持っていない場合
     */
    fun require(
        player: Player,
        permission: PlayerPermission,
    ) {
        if (!permission.has(player)) {
            throw RequirePermissionException(player.name)
        }
    }
}
