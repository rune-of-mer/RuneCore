package org.lyralis.runeCore.component.actionbar

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

/**
 * ステータス情報（所持金 + ping）を ActionBar に表示するプロバイダー．
 *
 * @param balanceProvider 所持金を取得する関数
 */
class StatusActionBarProvider(
    private val balanceProvider: (UUID) -> ULong,
) : PersistentActionBarProvider {
    override fun getContent(player: Player): Component {
        val balance = balanceProvider(player.uniqueId)
        val formattedBalance = formatBalance(balance)
        val ping = player.ping

        return Component
            .text("$formattedBalance Rune", NamedTextColor.GOLD)
            .append(Component.text(" | ", NamedTextColor.GRAY))
            .append(Component.text("${ping}ms", getPingColor(ping)))
    }

    private fun formatBalance(balance: ULong): String {
        val formatter = NumberFormat.getNumberInstance(Locale.JAPAN)
        return formatter.format(balance.toLong())
    }

    private fun getPingColor(ping: Int): NamedTextColor =
        when {
            ping < 50 -> NamedTextColor.GREEN
            ping < 100 -> NamedTextColor.YELLOW
            ping < 200 -> NamedTextColor.GOLD
            else -> NamedTextColor.RED
        }
}
