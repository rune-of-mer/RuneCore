package dev.m1sk9.runeCore.permission

/**
 * 必要な権限がプレイヤーに付与されていないときにスローされる例外．
 *
 * @param permissions 必要な権限ノードリスト
 */
class RequirePermissionException(
    permissions: List<String>,
) : Exception("権限が不足しています (${permissions.joinToString(", ")})")
