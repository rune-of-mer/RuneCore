package dev.m1sk9.runeCore.action

import dev.m1sk9.runeCore.component.errorMessage
import org.bukkit.entity.Player

class PlayerSessionAction(
    val player: Player,
) {
    private val result = listOf("予期せぬエラーが発生しました。公式Discordにて運営のサポートを受けてください。", "----", "${player.name}(${player.uniqueId})")

    fun setErrorKick() {
        player.kick(result.errorMessage())
    }
}
