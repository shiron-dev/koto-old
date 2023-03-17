package bot.command.core

import bot.Bot
import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.text.SimpleDateFormat
import java.util.*

class AboutCommand : Command() {
    override val commandName = "about"
    override val description = "Botについての説明を表示します。"
    override val commandPath = CommandPath("koto.core.about")

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val timeZoneJP = TimeZone.getTimeZone("Asia/Tokyo")
        val fmt = SimpleDateFormat()
        fmt.timeZone = timeZoneJP

        data.reply(
            "${Bot.jda.selfUser.asMention}だよ！よろしくね♪\n" +
                    "情報\n" +
                    "version: ${Bot.implementationVersion}\n" +
                    "ping: `${Bot.jda.gatewayPing}ms`\n" +
                    "started: ${fmt.format(Bot.stated)}"
        )
    }
}