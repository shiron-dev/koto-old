package bot.command.util

import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class HelloCommand : Command() {
    override val commandName = "hello"
    override val description = "Hello"
    override val commandPath = CommandPath("koto.util.hello")

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        data.reply("Hello ${event.user.asMention}!")
    }
}