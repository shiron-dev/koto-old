package bot.events

import bot.Bot
import bot.DISCORD_MESSAGE_URL_PREFIX
import bot.command.util.quote.sendQuoteMessage
import bot.command.util.read.vcRead
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MessageReceiveListener : ListenerAdapter() {

    private val messageUrlReg by lazy { Regex(pattern = "(?<!!)(?<!\\|\\|)?!?$DISCORD_MESSAGE_URL_PREFIX\\d+/\\d+/\\d+") }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        // VC Read
        if (Bot.vcReadMap[event.guild.idLong] == event.channel.idLong) {
            vcRead(event.message)
        }

        if (!event.channel.canTalk()) return

        // Quote
        val messageUrls = messageUrlReg.findAll(event.message.contentRaw).map { it.value }.toList()
        if (messageUrls.isNotEmpty()) {
            sendQuoteMessage(event.author, event.guild, messageUrls, event.channel)
        }
    }
}