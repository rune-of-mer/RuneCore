package org.lyralis.runeCore.permission

import org.bukkit.entity.Player

/**
 * プレイヤーの権限を定義するシールドクラス．
 * これらは node という Paper (Spigot/Bukkit) で扱われる文字列の権限ノードを各種持つ．
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
        object DebugMode : Admin("runecore.player.admin.debugmode")

        object DebugModeSwitchingGameMode : Admin("runecore.player.admin.debugmode.switchinggame")

        object ExperienceCommand : Admin("runecore.player.admin.command.experience")

        object ExperienceAddCommand : Admin("runecore.player.admin.command.experience.add")

        object CustomGiveCommand : Admin("runecore.player.admin.command.customgive")
    }

    fun has(player: Player): Boolean = player.hasPermission(node)
}
