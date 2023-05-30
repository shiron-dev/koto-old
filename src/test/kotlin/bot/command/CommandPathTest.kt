package bot.command

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CommandPathTest {

    @Test
    fun testValidCommandPath() {
        val commandPath = CommandPath.fromString("base.category.command") ?: fail()
        assertEquals("base", commandPath.base)
        assertEquals("category", commandPath.category)
        assertEquals("command", commandPath.commandName)
        assertNull(commandPath.subcommandName)
    }

    @Test
    fun testCommandPathWithSubcommand() {
        val commandPath = CommandPath.fromString("base.category.command.subcommand") ?: fail()
        assertEquals("base", commandPath.base)
        assertEquals("category", commandPath.category)
        assertEquals("command", commandPath.commandName)
        assertEquals("subcommand", commandPath.subcommandName)
    }

    @Test
    fun testInvalidCommandPath() {
        assertNull(CommandPath.fromString("base.category"))
        assertNull(CommandPath.fromString("base.category.command.subcommand.extra"))
    }

    @Test
    fun testToString() {
        val commandPath = CommandPath.fromString("base.category.command.subcommand")
        assertEquals("base.category.command.subcommand", commandPath.toString())
    }

    @Test
    fun testHashCode() {
        val commandPath = CommandPath.fromString("base.category.command")
        val commandPath1 = CommandPath.fromString("base.category.command")
        val commandPath2 = CommandPath.fromString("base.category.command.subcommand")

        assertEquals(commandPath.hashCode(), commandPath1.hashCode())
        assertNotEquals(commandPath.hashCode(), commandPath2.hashCode())
    }

    @Test
    fun testEquals() {
        val commandPath = CommandPath.fromString("base.category.command") ?: fail()
        val commandPath1 = CommandPath.fromString("base.category.command") ?: fail()
        val commandPath2 = CommandPath.fromString("base.category.command.subcommand") ?: fail()
        val commandPath3 = CommandPath.fromString("base.category.command") ?: fail()

        assertTrue(commandPath == commandPath1)
        assertFalse(commandPath == commandPath2)
        assertTrue(commandPath == commandPath3)
    }
}
