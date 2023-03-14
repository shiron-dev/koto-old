package bot.command.util

import bot.Bot
import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import bot.permission.permissionCheck
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun quoteFunction(
    user: User,
    guild: Guild,
    urls: List<String>
): Pair<List<MessageEmbed>, List<MessageEmbed>>? {
    if (permissionCheck(
            CommandPath("koto.util.quote"),
            user.idLong,
            guild.idLong
        ).runnable != true
    ) return null

    val ebs = mutableListOf<MessageEmbed>()
    val quoteEbs = mutableListOf<MessageEmbed>()
    val messages = runBlocking {
        urls.map { async { getMessage(it, guild) } }.awaitAll()
    }.filterNotNull()
    for (msg in messages) {
        val eb = EmbedBuilder()

        eb.setAuthor(msg.author.name, null, msg.author.avatarUrl ?: msg.author.defaultAvatarUrl)
        eb.setDescription(msg.contentRaw)
        ebs.add(eb.build())

        quoteEbs += msg.embeds
    }

    return Pair(ebs, quoteEbs)
}

private suspend fun getMessage(url: String, guild: Guild): Message? {
    val tokens = url.split("/")
    if (tokens.size < 7) return null
    val guildId = tokens[4].toLong()
    val channelId = tokens[5].toLong()
    val messageId = tokens[6].toLong()
    if (guild.idLong != guildId) return null
    return suspendCoroutine { continuation ->
        guild.getTextChannelById(channelId)?.retrieveMessageById(messageId)?.queue {
            continuation.resume(it)
        }
    }
}

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

        val quoteEbs =
            quoteFunction(event.user, event.guild!!, listOf(url)) ?: run {
                data.reply("引用するメッセージが取得できませんでした。")
                return
            }
        event.channel.sendMessage("${event.user.asMention}が引用").setEmbeds(quoteEbs.first).queue {
            data.reply("引用しました。\n${it.jumpUrl}")
        }
        if (quoteEbs.second.isNotEmpty()) event.channel.sendMessage("以下、引用のEmbed").setEmbeds(quoteEbs.second)
            .queue()
    }
}