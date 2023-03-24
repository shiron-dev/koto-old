package bot.events

import bot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.text.SimpleDateFormat
import java.util.*


class VoiceChannelJoinListener : ListenerAdapter() {
    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
        val vcJoinConfig =
            event.channelJoined?.idLong?.let { Bot.vcConfigDao.findByVCChannelIdAndGuildId(it, event.guild.idLong) }
        val vcLeftConfig =
            event.channelLeft?.idLong?.let { Bot.vcConfigDao.findByVCChannelIdAndGuildId(it, event.guild.idLong) }

        val eb = EmbedBuilder()

        eb.setAuthor(event.member.effectiveName, null, event.member.effectiveAvatarUrl)

        val date = Date()
        val timeZoneJP = TimeZone.getTimeZone("Asia/Tokyo")
        val fmt = SimpleDateFormat()
        fmt.timeZone = timeZoneJP
        eb.setFooter(fmt.format(date))

        fun mkEbJoin() {
            eb.setTitle("Join")
            eb.setDescription("${event.member.asMention}\nIN ${event.channelJoined!!.asMention}")
        }

        fun mkEbLeft() {
            eb.setTitle("Left")
            eb.setDescription("${event.member.asMention}\nOUT ${event.channelLeft!!.asMention}")
        }

        fun mkEbChange() {
            eb.setTitle("Change")
            eb.setDescription("${event.member.asMention}\nOUT ${event.channelLeft!!.asMention} -> IN ${event.channelJoined!!.asMention}")
        }

        fun getTextOrNewsChannel(channelId: Long): StandardGuildMessageChannel? {
            val guild = event.guild
            return guild.getTextChannelById(channelId) ?: guild.getNewsChannelById(channelId)
        }

        val vcJoinChanel = vcJoinConfig?.textChannelId?.let { getTextOrNewsChannel(it) }
        val vcLeftChannel = vcLeftConfig?.textChannelId?.let { getTextOrNewsChannel(it) }
        val vcChannel = vcJoinChanel ?: vcLeftChannel ?: return

        if (vcJoinConfig?.textChannelId == vcLeftConfig?.textChannelId) {
            if (event.channelJoined != null && event.channelLeft != null) {
                mkEbChange()
            } else if (event.channelJoined != null) {
                mkEbJoin()
            } else if (event.channelLeft != null) {
                mkEbLeft()
            }
            vcChannel.sendMessage("").setEmbeds(eb.build()).queue()
        } else {
            if (event.channelJoined != null && vcJoinConfig != null) {
                mkEbJoin()
                vcJoinChanel?.sendMessage("")?.setEmbeds(eb.build())
                    ?.queue()
            }
            if (event.channelLeft != null && vcLeftConfig != null) {
                mkEbLeft()
                vcLeftChannel?.sendMessage("")?.setEmbeds(eb.build())
                    ?.queue()
            }
        }
    }
}