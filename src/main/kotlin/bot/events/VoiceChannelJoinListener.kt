package bot.events

import bot.Bot
import net.dv8tion.jda.api.EmbedBuilder
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
        val vcConfig = vcJoinConfig ?: vcLeftConfig ?: return

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
        if (vcJoinConfig?.textChannelId == vcLeftConfig?.textChannelId) {
            if (event.channelJoined != null && event.channelLeft != null) {
                eb.setTitle("Change")
                eb.setDescription("${event.member.asMention}\nOUT ${event.channelLeft!!.asMention} -> IN ${event.channelJoined!!.asMention}")
            } else if (event.channelJoined != null) {
                mkEbJoin()
            } else if (event.channelLeft != null) {
                mkEbLeft()
            }
            event.guild.getTextChannelById(vcConfig.textChannelId)?.sendMessage("")?.setEmbeds(eb.build())?.queue()
        } else {
            if (event.channelJoined != null && vcJoinConfig != null) {
                mkEbJoin()
                event.guild.getTextChannelById(vcJoinConfig.textChannelId)?.sendMessage("")?.setEmbeds(eb.build())
                    ?.queue()
            }
            if (event.channelLeft != null && vcLeftConfig != null) {
                mkEbLeft()
                event.guild.getTextChannelById(vcLeftConfig.textChannelId)?.sendMessage("")?.setEmbeds(eb.build())
                    ?.queue()
            }
        }
    }
}