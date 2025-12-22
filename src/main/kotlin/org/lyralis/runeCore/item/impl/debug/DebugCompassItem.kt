package org.lyralis.runeCore.item.impl.debug

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.item.CustomItem
import org.lyralis.runeCore.item.CustomItemType
import org.lyralis.runeCore.item.ItemRarity
import org.lyralis.runeCore.item.model.CustomItemAction
import org.lyralis.runeCore.permission.Permission
import org.lyralis.runeCore.permission.RequirePermissionException
import org.lyralis.runeCore.permission.requirePermissionAll

/**
 * 右クリック + スニークでゲームモードを切り替えるアイテム
 */
object DebugCompassItem : CustomItem, CustomItemType.Usable {
    override val id = "debug_compass"
    override val displayName = "ゲームモード切り替えコンパス"
    override val lore = listOf("右クリック + スニーク: ゲームモード切り替え")
    override val material = Material.COMPASS
    override val rarity = ItemRarity.ADMIN
    override val requiredPermission = Permission.Admin.DebugMode

    override fun onUse(
        player: Player,
        action: CustomItemAction,
    ): Boolean {
        if (action != CustomItemAction.RIGHT_CLICK) return false
        if (!player.isSneaking) return false

        return try {
            player.requirePermissionAll {
                +Permission.Admin.DebugMode
                +Permission.Admin.DebugModeSwitchingGameMode
            }

            player.apply {
                gameMode =
                    when (player.gameMode) {
                        GameMode.SURVIVAL -> GameMode.CREATIVE
                        GameMode.CREATIVE -> GameMode.SURVIVAL
                        else -> GameMode.SURVIVAL
                    }
                ActionBarManager.showTemporaryNotification(
                    this,
                    "ゲームモードを変更しました: ${player.gameMode}".systemMessage(),
                )
            }

            true
        } catch (e: RequirePermissionException) {
            player.sendMessage((e.message ?: "権限が不足しています").errorMessage())
            false
        }
    }
}
