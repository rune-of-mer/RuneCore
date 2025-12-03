package dev.m1sk9.runeCore.player.permission

import org.bukkit.entity.Player

/**
 * プレイヤーの権限を定義するシールドクラス．
 * これらは node という Paper (Spigot/Bukkit) で扱われる文字列の権限ノードを各種持ち，
 * プレイヤーの権限管理に使用される．
 */
sealed class PlayerPermission(
    val node: String,
) {
    /**
     * プレイヤーの役割を定義するシールドクラス
     * node は `runecore.player.*` で定義される．
     */
    sealed class Role(
        node: String,
    ) : PlayerPermission(node) {
        object Player : Role("runecore.role.player")

        object Admin : Role("runecore.role.admin")
    }

    /**
     * 基本的なプレイヤー権限を定義するシールドクラス
     * node は `runecore.player.basic.*` で定義される．
     */
    sealed class Basic(
        node: String,
    ) : PlayerPermission(node) {
        // TODO: Define basic permissions
    }

    /**
     * 管理者向けのプレイヤー権限を定義するシールドクラス
     * node は `runecore.player.admin.*` で定義される．
     */
    sealed class Admin(
        node: String,
    ) : PlayerPermission(node) {
        // ---- Debug Mode Permissions ----
        // デバックモードの使用許可
        object DebugMode : Admin("runecore.player.admin.debugmode")

        // デバックモード: ゲームモード切り替え
        object DebugModeSwitchingGameMode : Admin("runecore.player.admin.debugmode.switchinggame")
    }

    fun has(player: Player): Boolean = player.hasPermission(node)
}
