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
        OptionData(OptionType.CHANNEL, "vc", "参加を通知してほしいボイスチャンネル", true),
        OptionData(OptionType.CHANNEL, "text", "通知を表示するテキストチャンネル", true),
    )

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val vcChannel = event.getOption("vc")?.asChannel
        val textChannel = event.getOption("text")?.asChannel
        if (vcChannel == null || textChannel == null) {
            data.reply("`vc`、`text`の引数は必ず設定してください。")
            return
        }
        if (vcChannel.type != ChannelType.VOICE && vcChannel.type != ChannelType.STAGE) {
            data.reply("`vc`の引数はボイスチャンネルか、ステージチャンネルにしてください。")
            return
        }
        if (textChannel.type != ChannelType.TEXT) {
            data.reply("`text`の引数はテキストチャンネルにしてください。")
            return
        }

        val vcConfig = Bot.vcConfigDao.findByVCChannelIdAndGuildId(vcChannel.idLong, event.guild!!.idLong)
        if (vcConfig == null) {
            val vc = VCConfig(
                vcChannelId = vcChannel.idLong,
                textChannelId = textChannel.idLong,
                guildId = event.guild!!.idLong
            )
            Bot.vcConfigDao.createSave(vc)
            data.reply("${vcChannel.asMention}の参加通知を${textChannel.asMention}に設定しました。")
        } else {
            data.reply("${vcChannel.asMention}の参加通知を<#${vcConfig.textChannelId}>から${textChannel.asMention}に更新しました。")
            vcConfig.textChannelId = textChannel.idLong
            vcConfig.save()
        }
    }
}