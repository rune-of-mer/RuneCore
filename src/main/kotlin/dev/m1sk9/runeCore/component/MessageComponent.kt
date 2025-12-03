package dev.m1sk9.runeCore.component

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

@JvmInline
value class MessageComponent(
    val message: String,
) {
    fun systemMessage(): Component = Component.text(message).color(NamedTextColor.YELLOW)

    fun errorMessage(): Component = Component.text(message).color(NamedTextColor.RED)

    fun infoMessage(): Component = Component.text(message).color(NamedTextColor.AQUA)

    fun debugMessage(): Component = Component.text(message).color(NamedTextColor.LIGHT_PURPLE)

    fun customMessage(color: NamedTextColor): Component = Component.text(message).color(color)
}
