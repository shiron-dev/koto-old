package bot.command.util.quote

import bot.Bot
import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class QuoteCommand : Command() {
    override val commandName = "quote"
    override val description = "引用"
    override val commandPath = CommandPath("koto.util.quote")
    override val commandOptions = listOf(OptionData(OptionType.STRING, "url", "引用するメッセージのURL", true))

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val url = event.getOption("url")?.asString ?: run {
            data.reply("引数`url`を指定してください。")
            return
        }

        if (!event.channel.asTextChannel().canTalk()) {
            data.reply("${Bot.jda.selfUser.asMention}に${event.channel.asMention}で発言する権限がありません。")
            return
        }

        sendQuoteMessage(event.user, event.guild!!, listOf(url), event.channel) {
            it?.let {
                data.reply("引用しました。\n${it.jumpUrl}")
            } ?: data.reply("引用するメッセージが取得できませんでした。")
        }
    }
}