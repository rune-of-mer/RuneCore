package org.lyralis.runeCore.component.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

val SYSTEM_COLOR: NamedTextColor = NamedTextColor.YELLOW
val ERROR_COLOR: NamedTextColor = NamedTextColor.RED
val INFO_COLOR: NamedTextColor = NamedTextColor.AQUA
val DEBUG_COLOR: NamedTextColor = NamedTextColor.LIGHT_PURPLE

fun String.systemMessage(): Component = Component.text(this).color(SYSTEM_COLOR)

fun String.errorMessage(): Component = Component.text(this).color(ERROR_COLOR)

fun String.infoMessage(): Component = Component.text(this).color(INFO_COLOR)

fun String.debugMessage(): Component = Component.text(this).color(DEBUG_COLOR)

fun String.customMessage(color: NamedTextColor): Component = Component.text(this).color(color)

fun List<String>.systemMessage(): Component = Component.text(this.joinToString("\n")).color(SYSTEM_COLOR)

fun List<String>.errorMessage(): Component = Component.text(this.joinToString("\n")).color(ERROR_COLOR)

fun List<String>.infoMessage(): Component = Component.text(this.joinToString("\n")).color(INFO_COLOR)

fun List<String>.debugMessage(): Component = Component.text(this.joinToString("\n")).color(DEBUG_COLOR)

fun List<String>.customMessage(color: NamedTextColor): Component = Component.text(this.joinToString("\n")).color(color)
