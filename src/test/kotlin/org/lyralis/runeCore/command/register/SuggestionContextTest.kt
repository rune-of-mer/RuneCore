package org.lyralis.runeCore.command.register

import io.mockk.every
import io.mockk.mockk
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SuggestionContextTest {
    @Test
    fun `should create SuggestionContext with correct properties`() {
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha event1",
                args = listOf("event1"),
                currentArg = "event1",
            )

        assertEquals("/gacha event1", context.input)
        assertEquals(listOf("event1"), context.args)
        assertEquals("event1", context.currentArg)
        assertEquals(mockSender, context.sender)
    }

    @Test
    fun `should return player when sender is Player`() {
        val mockSource = mockk<CommandSourceStack>()
        val mockPlayer = mockk<Player>()
        every { mockSource.sender } returns mockPlayer

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha",
                args = emptyList(),
                currentArg = "",
            )

        assertNotNull(context.player)
        assertEquals(mockPlayer, context.player)
    }

    @Test
    fun `should return null when sender is not Player`() {
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha",
                args = emptyList(),
                currentArg = "",
            )

        assertNull(context.player)
    }

    @Test
    fun `filterStartsWith should filter candidates by currentArg`() {
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

        val candidates = listOf("event1", "event2", "sample", "test")
        val filtered = context.filterStartsWith(candidates)

        assertEquals(listOf("event1", "event2"), filtered)
    }

    @Test
    fun `filterStartsWith should be case insensitive`() {
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

        val candidates = listOf("event1", "Event2", "SAMPLE", "test")
        val filtered = context.filterStartsWith(candidates)

        assertEquals(listOf("event1", "Event2"), filtered)
    }

    @Test
    fun `filterStartsWith should return empty list when no matches`() {
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha xyz",
                args = listOf("xyz"),
                currentArg = "xyz",
            )

        val candidates = listOf("event1", "event2", "sample", "test")
        val filtered = context.filterStartsWith(candidates)

        assertEquals(emptyList(), filtered)
    }

    @Test
    fun `filterStartsWith should return all candidates when currentArg is empty`() {
        val mockSource = mockk<CommandSourceStack>()
        val mockSender = mockk<CommandSender>()
        every { mockSource.sender } returns mockSender

        val context =
            SuggestionContext(
                source = mockSource,
                input = "/gacha ",
                args = listOf(""),
                currentArg = "",
            )

        val candidates = listOf("event1", "event2", "sample", "test")
        val filtered = context.filterStartsWith(candidates)

        assertEquals(candidates, filtered)
    }
}
