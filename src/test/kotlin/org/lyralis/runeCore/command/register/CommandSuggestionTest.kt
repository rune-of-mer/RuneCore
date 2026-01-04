package org.lyralis.runeCore.command.register

import io.mockk.every
import io.mockk.mockk
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.lyralis.runeCore.command.RuneCommand
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandSuggestionTest {
    /**
     * Test command that provides suggestions for items
     */
    private class TestItemCommand : RuneCommand {
        override val name = "item"
        override val description = "Test item command"

        override fun execute(context: RuneCommandContext): CommandResult = CommandResult.Success()

        override fun suggest(context: SuggestionContext): List<String> {
            val items = listOf("diamond", "iron", "gold", "stone", "wood")
            return when (context.args.size) {
                1 -> context.filterStartsWith(items)
                else -> emptyList()
            }
        }
    }

    /**
     * Test command with subcommands that provide suggestions
     */
    private class TestGachaCommand : RuneCommand {
        override val name = "gacha"
        override val description = "Test gacha command"

        override fun execute(context: RuneCommandContext): CommandResult = CommandResult.Success()

        override fun suggest(context: SuggestionContext): List<String> {
            val events = listOf("event1", "event2", "event_special")
            return when (context.args.size) {
                1 -> context.filterStartsWith(events)
                else -> emptyList()
            }
        }
    }

    @Test
    fun `suggest should return all items when currentArg is empty`() {
        val command = TestItemCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/item ",
                args = listOf(""),
                currentArg = "",
            )

        val suggestions = command.suggest(context)
        assertEquals(5, suggestions.size)
        assertTrue(suggestions.contains("diamond"))
        assertTrue(suggestions.contains("iron"))
        assertTrue(suggestions.contains("gold"))
        assertTrue(suggestions.contains("stone"))
        assertTrue(suggestions.contains("wood"))
    }

    @Test
    fun `suggest should filter items by prefix`() {
        val command = TestItemCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/item d",
                args = listOf("d"),
                currentArg = "d",
            )

        val suggestions = command.suggest(context)
        assertEquals(listOf("diamond"), suggestions)
    }

    @Test
    fun `suggest should return empty list when no matches`() {
        val command = TestItemCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/item xyz",
                args = listOf("xyz"),
                currentArg = "xyz",
            )

        val suggestions = command.suggest(context)
        assertTrue(suggestions.isEmpty())
    }

    @Test
    fun `suggest should return empty list for second argument`() {
        val command = TestItemCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/item diamond 10",
                args = listOf("diamond", "10"),
                currentArg = "10",
            )

        val suggestions = command.suggest(context)
        assertTrue(suggestions.isEmpty())
    }

    @Test
    fun `gacha command should suggest events`() {
        val command = TestGachaCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha ev",
                args = listOf("ev"),
                currentArg = "ev",
            )

        val suggestions = command.suggest(context)
        assertEquals(3, suggestions.size)
        assertTrue(suggestions.contains("event1"))
        assertTrue(suggestions.contains("event2"))
        assertTrue(suggestions.contains("event_special"))
    }

    @Test
    fun `gacha command should filter by prefix correctly`() {
        val command = TestGachaCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha event_",
                args = listOf("event_"),
                currentArg = "event_",
            )

        val suggestions = command.suggest(context)
        assertEquals(listOf("event_special"), suggestions)
    }

    @Test
    fun `suggest should handle case insensitive filtering`() {
        val command = TestGachaCommand()
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha EV",
                args = listOf("EV"),
                currentArg = "EV",
            )

        val suggestions = command.suggest(context)
        assertEquals(3, suggestions.size)
    }
}
