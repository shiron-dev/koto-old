package bot.command.core

import bot.Bot
import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class PingCommand : Command() {
    override val commandName = "ping"
    override val description = "Ping"
    override val commandPath = CommandPath("koto.core.ping")

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        data.reply("Pong! `${Bot.jda.gatewayPing}ms`")
    }
}