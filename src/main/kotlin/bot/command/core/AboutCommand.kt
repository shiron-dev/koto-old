package bot.command.core

import bot.Bot
import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import bot.dotenv
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

        val now = Date()
        val diff = now.time - Bot.stated.time
        val format = SimpleDateFormat("HHHH mm")
        val formattedTime = format.format(Date(diff))

        data.reply(
            "${Bot.jda.selfUser.asMention}だよ！よろしくね♪\n" +
                    (if (Bot.isDevMode) ":warning: 開発モード:warning: \n" else "") +
                    (dotenv["SUPPORT_SERVER_URL"]?.let { "サポートサーバー: $it\n" } ?: "") +
                    "> 情報\n" +
                    "version: ${Bot.implementationVersion}\n" +
                    "ping: `${Bot.jda.gatewayPing}ms`\n" +
                    "started: ${fmt.format(Bot.stated)} ($formattedTime)"
        )
    }
}