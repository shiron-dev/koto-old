package bot.command.hello

import bot.command.Command
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class HelloCommand : Command() {
    override val name = "hello"
    override val description = "Hello"
    override val commandPath = CommandPath("koto.util.hello")

    override fun onSlashCommand(event: SlashCommandInteractionEvent) {
        event.reply("Hello ${event.user.name}!").setEphemeral(true).queue()
    }
}