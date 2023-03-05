package bot.command.hello

import bot.command.Command
import bot.command.CommandPath
import bot.user.DiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class HelloCommand : Command() {
    override val commandName = "hello"
    override val description = "Hello"
    override val commandPath = CommandPath("koto.util.hello")

    override fun onSlashCommand(event: SlashCommandInteractionEvent, user: DiscordUser) {
//${event.guild?.publicRole?.asMention}
        event.hook.editOriginal("Hello ${event.user.asMention}!").queue()
    }
}