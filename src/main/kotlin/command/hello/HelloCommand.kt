package command.hello

import command.Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class HelloCommand : Command() {
    override val name = "hello"
    override val description = "Hello"

    override fun onSlashCommand(event: SlashCommandInteractionEvent) {
        event.reply("Hello!").setEphemeral(true).queue()
    }
}