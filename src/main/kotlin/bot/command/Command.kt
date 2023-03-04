package bot.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class Command : ListenerAdapter() {

    abstract val name: String
    abstract val description: String
    abstract val commandPath: CommandPath

    val slashCommandData: SlashCommandData
        get() = Commands.slash(name, description)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == name) {
            onSlashCommand(event)
        }
    }

    abstract fun onSlashCommand(event: SlashCommandInteractionEvent)

}

class CommandPath(commandPath: String) {
    val base: String
    val category: String
    val commandName: String

    init {
        val token = commandPath.split(".")
        base = token[0]
        category = token[1]
        commandName = token[2]
    }

    override fun toString(): String {
        return "$base.$category.$commandName"
    }

    override fun hashCode(): Int {
        return (this.base.hashCode() * 31 + this.category.hashCode()) * 31 + this.commandName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is CommandPath) return false
        return this.base == other.base && this.category == other.category && this.commandName == other.commandName
    }
}