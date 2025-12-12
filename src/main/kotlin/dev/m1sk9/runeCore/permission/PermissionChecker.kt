package dev.m1sk9.runeCore.permission

import org.bukkit.entity.Player

class RequirePermissionException(
    permissions: List<String>,
) : Exception("権限が不足しています (${permissions.joinToString(", ")})")

@DslMarker
annotation class PermissionDsl

/**
 * 権限を収集するためのDSLコレクター．
 */
@PermissionDsl
class PermissionCollector {
    internal val permissions = mutableListOf<Permission>()

    operator fun Permission.unaryPlus() {
        permissions.add(this)
    }
}

/**
 * 指定されたいずれかの権限をプレイヤーが持っているかを確認します．
 *
 * @param block 権限を指定するDSLブロック
 * @return 指定された権限のいずれかをプレイヤーが持っている場合に true を返します．
 *
 * 使用例:
 * ```
 * player.hasAny {
 *     +PlayerPermission.Admin.DebugMode
 * }
 * ```
 */
fun Player.hasPermissionAny(block: PermissionCollector.() -> Unit): Boolean {
    val collector = PermissionCollector().apply(block)
    return collector.permissions.any { it.has(this) }
}

/**
 * 指定されたすべての権限をプレイヤーが持っているかを確認します．
 *
 * @param block 権限を指定するDSLブロック
 * @return 指定されたすべての権限をプレイヤーが持っている場合に true を返します．
 *
 * 使用例:
 * ```
 * player.hasAll {
 *     +PlayerPermission.Admin.DebugMode
 *     +PlayerPermission.Admin.DebugModeSwitchingGameMode
 * }
 * ```
 */
fun Player.hasPermissionAll(block: PermissionCollector.() -> Unit): Boolean {
    val collector = PermissionCollector().apply(block)
    return collector.permissions.all { it.has(this) }
}

/**
 * 指定されたいずれかの権限をプレイヤーが持っていることを要求します．
 * プレイヤーが権限を持っていない場合、RequirePermissionException をスローします．
 *
 * @param block 権限を指定するDSLブロック
 * @throws RequirePermissionException プレイヤーが指定された権限のいずれも持っていない場合
 *
 * 使用例:
 * ```
 * player.requirePermissionAny {
 *     +Permission.Admin.DebugMode
 * }
 * ```
 */
fun Player.requirePermissionAny(block: PermissionCollector.() -> Unit) {
    val collector = PermissionCollector().apply(block)
    if (!collector.permissions.any { it.has(this) }) {
        throw RequirePermissionException(collector.permissions.map { it.node })
    }
}

/**
 * 指定されたすべての権限をプレイヤーが持っていることを要求します．
 * プレイヤーが権限を持っていない場合、RequirePermissionException をスローします．
 *
 * @param block 権限を指定するDSLブロック
 * @throws RequirePermissionException プレイヤーが指定された権限のいずれかを持っていない場合
 *
 * 使用例:
 * ```
 * player.requirePermissionAll {
 *     +Permission.Admin.DebugMode
 *     +Permission.Admin.DebugModeSwitchingGameMode
 * }
 * ```
 */
fun Player.requirePermissionAll(block: PermissionCollector.() -> Unit) {
    val collector = PermissionCollector().apply(block)
    if (!collector.permissions.all { it.has(this) }) {
        throw RequirePermissionException(collector.permissions.map { it.node })
    }
}
