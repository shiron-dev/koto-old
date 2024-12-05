package bot.command.util.quote

import bot.command.CommandPath
import bot.domain.permission.permissionCheck
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.utils.FileUpload
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private fun getQuoteEbs(
    user: User,
    guild: Guild,
    urls: List<String>
): Triple<List<MessageEmbed>, List<MessageEmbed>, List<FileUpload>>? {
    if (CommandPath.fromString("koto.util.quote")?.let {
            permissionCheck(
                it,
                user.idLong,
                guild.idLong
            ).runnable
        } != true
    ) return null
    val ebs = mutableListOf<MessageEmbed>()
    val quoteEbs = mutableListOf<MessageEmbed>()
    val messages = runBlocking {
        urls.map { async { getMessage(it, guild) } }.awaitAll()
    }.filterNotNull()
    val files = mutableListOf<Message.Attachment>()
    for (msg in messages) {
        val eb = EmbedBuilder()

        eb.setAuthor(msg.author.name, null, msg.author.avatarUrl ?: msg.author.defaultAvatarUrl)
        eb.setDescription(msg.contentRaw)
        ebs.add(eb.build())

        quoteEbs += msg.embeds

        files += msg.attachments
    }

    val ups = files.map { FileUpload.fromData(it.proxy.download().get(), it.fileName) }
    return Triple(ebs, quoteEbs, ups)
}

private suspend fun getMessage(url: String, guild: Guild): Message? {
    val tokens = url.split("/")
    if (tokens.size < 7) return null
    val guildId = tokens[4].toLong()
    val channelId = tokens[5].toLong()
    val messageId = tokens[6].toLong()
    if (guild.idLong != guildId) return null
    val channel = guild.getChannelById(GuildMessageChannel::class.java, channelId)

    return suspendCoroutine { continuation ->
        channel?.retrieveMessageById(messageId)?.queue {
            continuation.resume(it)
        }
    }
}

fun sendQuoteMessage(
    user: User,
    guild: Guild,
    urls: List<String>,
    sendChannel: MessageChannel,
    onSent: (message: Message?) -> Unit = {}
) {
    val quoteEbs =
        getQuoteEbs(user, guild, urls) ?: run {
            onSent(null)
            return
        }

    if (quoteEbs.first.isEmpty() && quoteEbs.second.isEmpty() && quoteEbs.third.isEmpty()) {
        onSent(null)
        return
    }

    sendChannel.sendMessage("${user.asMention}が引用").setEmbeds(quoteEbs.first).queue {
        onSent(it)
        if (quoteEbs.second.isNotEmpty()) sendChannel.sendMessage("> 以下、引用のEmbed").setEmbeds(quoteEbs.second)
            .queue()

        if (quoteEbs.third.isNotEmpty()) sendChannel.sendMessage("> 以下、引用のファイル").addFiles(quoteEbs.third).queue()
    }
}
