package bot.events

import bot.DISCORD_MESSAGE_URL_PREFIX
import bot.command.util.quote.sendQuoteMessage
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MessageReceiveListener : ListenerAdapter() {

    private val messageUrlReg by lazy { Regex(pattern = "(?<!!)(?<!\\|\\|)?!?$DISCORD_MESSAGE_URL_PREFIX\\d+/\\d+/\\d+") }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (!event.channel.canTalk()) return
        val messageUrls = messageUrlReg.findAll(event.message.contentRaw).map { it.value }.toList()
        if (messageUrls.isNotEmpty()) {
            sendQuoteMessage(event.author, event.guild, messageUrls, event.channel)
        }
    }
}