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
        object CustomGiveCommand : Admin("runecore.player.admin.command.customgive")

        object DebugMode : Admin("runecore.player.admin.debugmode")

        object DebugModeSwitchingGameMode : Admin("runecore.player.admin.debugmode.switchinggame")

        object ExperienceCommand : Admin("runecore.player.admin.command.experience")

        object ExperienceAddCommand : Admin("runecore.player.admin.command.experience.add")

        object MoneyCommand : Admin("runecore.player.admin.command.money")

        object MoneySetCommand : Admin("runecore.player.admin.command.money.set")

        object MoneyAddCommand : Admin("runecore.player.admin.command.money.add")

        object MoneyReduceCommand : Admin("runecore.player.admin.command.money.reduce")

        object WarpAddPoint : Admin("runecore.player.admin.command.warp.addpoint")
    }

    fun has(player: Player): Boolean = player.hasPermission(node)
}
