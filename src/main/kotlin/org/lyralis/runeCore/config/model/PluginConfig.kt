package org.lyralis.runeCore.config.model

data class PluginConfig(
    val patchNoteURL: String = "https://example.com",
    val motd: List<String> = listOf(" --- Welcome to Rune of Mer! --- "),
    val firstMotd: List<String> = listOf("Rune of Mer へようこそ!"),
    val tutorialRune: Long = 1000,
    val tutorialExp: Long = 1000
)
