package bot.command.util.vc

import bot.Bot
import bot.command.CommandEventData
import bot.command.Subcommand
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class VCSetCommand : Subcommand() {
    override val subcommandName = "set"
    override val description = "VCの参加通知の設定をします。"
    override val subcommandOptions = listOf(
        OptionData(OptionType.CHANNEL, "vc", "参加を通知してほしいボイスチャンネル/カテゴリ", true),
        OptionData(OptionType.CHANNEL, "text", "通知を表示するテキストチャンネル", true),
    )

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val vcChannel = event.getOption("vc")?.asChannel
        val textChannel = event.getOption("text")?.asChannel
        if (vcChannel == null || textChannel == null) {
            data.reply("`vc`、`text`の引数は必ず設定してください。")
            return
        }
        if (vcChannel.type != ChannelType.VOICE && vcChannel.type != ChannelType.STAGE && vcChannel.type != ChannelType.CATEGORY) {
            data.reply("`vc`の引数はボイスチャンネルか、ステージチャンネル、ボイスチャンネルを含むカテゴリにしてください。")
            return
        }
        if (textChannel.type != ChannelType.TEXT && textChannel.type != ChannelType.NEWS) {
            data.reply("`text`の引数はテキストチャンネルか、アナウンスチャンネルにしてください。")
            return
        }

        val vcChannels = if (vcChannel.type == ChannelType.CATEGORY) {
            vcChannel.asCategory().channels.filter { it.type == ChannelType.VOICE || it.type == ChannelType.STAGE }
        } else {
            listOf(vcChannel)
        }

        val outStr = vcChannels.joinToString("\n") {
            val vcConfig = Bot.vcConfigDao.findByVCChannelIdAndGuildId(it.idLong, event.guild!!.idLong)
            if (vcConfig == null) {
                val vc = VCConfig(
                    vcChannelId = it.idLong,
                    textChannelId = textChannel.idLong,
                    guildId = event.guild!!.idLong
                )
                Bot.vcConfigDao.createSave(vc)
                "${it.asMention}の参加通知を${textChannel.asMention}に設定しました。"
            } else {
                vcConfig.textChannelId = textChannel.idLong
                vcConfig.save()
                "${it.asMention}の参加通知を<#${vcConfig.textChannelId}>から${textChannel.asMention}に更新しました。"
            }
        }
        data.reply(outStr)
    }
}