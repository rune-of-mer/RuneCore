package org.lyralis.runeCore.listener

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.model.shop.ShopItemRegistry
import org.lyralis.runeCore.gui.impl.shop.ShopMainGui
import org.lyralis.runeCore.gui.impl.shop.ShopSearchGui
import org.lyralis.runeCore.gui.impl.shop.ShopSearchManager

/**
 * ショップ検索のチャット入力を受け取るリスナー
 */
class ShopChatInputListener(
    private val plugin: Plugin,
    private val moneyService: MoneyService,
    private val shopMainGui: ShopMainGui,
) : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onChat(event: AsyncChatEvent) {
        val player = event.player

        if (!ShopSearchManager.isAwaiting(player)) return

        event.isCancelled = true
        ShopSearchManager.stopAwaiting(player)

        val message = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (message.equals("cancel", ignoreCase = true)) {
            player.sendMessage("検索をキャンセルしました".infoMessage())
            plugin.server.scheduler.runTask(
                plugin,
                Runnable {
                    shopMainGui.open(player)
                },
            )
            return
        }

        val results = ShopItemRegistry.search(message)

        plugin.server.scheduler.runTask(
            plugin,
            Runnable {
                if (results.isEmpty()) {
                    player.sendMessage("'$message' に一致するアイテムが見つかりませんでした".errorMessage())
                    shopMainGui.open(player)
                } else {
                    ShopSearchGui(moneyService, message, results, shopMainGui).open(player)
                }
            },
        )
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        ShopSearchManager.cleanup(event.player.uniqueId)
    }
}
