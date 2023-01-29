package command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class Command : ListenerAdapter() {

    abstract val name: String
    abstract val description: String

    val slashCommandData: SlashCommandData
        get() = Commands.slash(name, description)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == name) {
            onSlashCommand(event)
        }
    }

    abstract fun onSlashCommand(event: SlashCommandInteractionEvent)

}