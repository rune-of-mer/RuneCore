package org.lyralis.runeCore.component.action

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.lyralis.runeCore.permission.Permission
import org.lyralis.runeCore.permission.requirePermissionAll

/**
 * プレイヤーのデバッグ操作に関するクラス．
 *
 * @param player ターゲットとなるプレイヤー
 */
class PlayerDebugAction(
    val player: Player,
) {
    /**
     * プレイヤーのゲームモードを以下のルーティングで切り替える．
     *
     * サバイバルモード → クリエイティブモード → アドベンチャーモード
     *
     * これを実行するには [player] が [Permission.Admin.DebugMode] と [Permission.Admin.DebugModeSwitchingGameMode] の権限を持っている必要があります．
     *
     * @throws [org.lyralis.runeCore.permission.RequirePermissionException] [player] が [Permission.Admin.DebugMode] と [Permission.Admin.DebugModeSwitchingGameMode] の権限を持っていなかった場合にスローされます．
     */
    fun changeGameMode() {
        player.requirePermissionAll {
            +Permission.Admin.DebugMode
            +Permission.Admin.DebugModeSwitchingGameMode
        }

        player.gameMode =
            when (player.gameMode) {
                GameMode.SURVIVAL -> GameMode.CREATIVE
                GameMode.CREATIVE -> GameMode.ADVENTURE
                GameMode.ADVENTURE -> GameMode.SURVIVAL
                else -> GameMode.SURVIVAL
            }
    }
}
