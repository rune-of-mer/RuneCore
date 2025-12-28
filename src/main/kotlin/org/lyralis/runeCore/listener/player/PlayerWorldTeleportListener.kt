package org.lyralis.runeCore.listener.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.lyralis.runeCore.config.ConfigManager

class PlayerWorldTeleportListener : Listener {
    private val config = ConfigManager.get()

    @EventHandler
    fun onTeleport(event: PlayerChangedWorldEvent) {
        val player = event.player
        val world = player.world
        val confinedWorld = config.world

        val titleItem: Triple<String, String, NamedTextColor> =
            when (world.name) {
                confinedWorld.pvp.name -> {
                    Triple("PvP ワールド", "PvP/アイテムロストなしのバトル型コンテンツ", NamedTextColor.LIGHT_PURPLE)
                }

                confinedWorld.life.name -> {
                    Triple("生活ワールド", "家を建てたり，町を作ったりできる生活型コンテンツ", NamedTextColor.AQUA)
                }

                else -> {
                    Triple("資源ワールド", "生活のために必要な資源を確保できる生活型コンテンツ", NamedTextColor.GREEN)
                }
            }

        player.showTitle(
            Title.title(
                Component.text(titleItem.first, titleItem.third),
                Component.text(titleItem.second),
            ),
        )
    }
}
