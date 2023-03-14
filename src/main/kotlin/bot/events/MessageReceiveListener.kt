package bot.events

import bot.DISCORD_MESSAGE_URL_PREFIX
import bot.command.util.quoteFunction
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MessageReceiveListener : ListenerAdapter() {

    private val messageUrlReg by lazy { Regex(pattern = "(?<!!)(?<!\\|\\|)?!?$DISCORD_MESSAGE_URL_PREFIX\\d+/\\d+/\\d+") }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (!event.channel.canTalk()) return
        val messageUrls = messageUrlReg.findAll(event.message.contentRaw).map { it.value }.toList()
        if (messageUrls.isNotEmpty()) {
            val quoteEbs =
                quoteFunction(event.author, event.guild, messageUrls) ?: return
            event.channel.sendMessage("${event.author.asMention}が引用").setEmbeds(quoteEbs.first).queue()
            if (quoteEbs.second.isNotEmpty()) event.channel.sendMessage("以下、引用のEmbed").setEmbeds(quoteEbs.second)
                .queue()
        }
    }
}