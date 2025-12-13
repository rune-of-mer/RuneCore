package dev.m1sk9.runeCore.permission

import org.bukkit.entity.Player

/**
 * プレイヤーの権限を定義するシールドクラス．
 * これらは node という Paper (Spigot/Bukkit) で扱われる文字列の権限ノードを各種持ち，
 * プレイヤーの権限管理に使用される．
 */
sealed class Permission(
    val node: String,
) {
    sealed class Role(
        node: String,
    ) : Permission(node) {
        object Player : Role("runecore.role.player")

        object Admin : Role("runecore.role.admin")
    }

    sealed class Basic(
        node: String,
    ) : Permission(node)

    sealed class Admin(
        node: String,
    ) : Permission(node) {
        // ---- Debug Mode Permissions ----
        // デバックモードの使用許可
        object DebugMode : Admin("runecore.player.admin.debugmode")

        // デバックモード: ゲームモード切り替え
        object DebugModeSwitchingGameMode : Admin("runecore.player.admin.debugmode.switchinggame")
    }

    fun has(player: Player): Boolean = player.hasPermission(node)
}
