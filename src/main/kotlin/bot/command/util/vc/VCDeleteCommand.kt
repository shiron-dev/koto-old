package bot.command.util.vc

import bot.Bot
import bot.command.CommandEventData
import bot.command.Subcommand
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class VCDeleteCommand : Subcommand() {
    override val subcommandName = "delete"
    override val description = "VCの参加通知の設定を削除。"
    override val subcommandOptions = listOf(
        OptionData(OptionType.CHANNEL, "vc", "参加通知をやめてほしいボイスチャンネル/カテゴリ", true),
    )

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val vcChannel = event.getOption("vc")?.asChannel
        if (vcChannel == null) {
            data.reply("`vc`の引数は必ず設定してください。")
            return
        }
        if (vcChannel.type != ChannelType.VOICE && vcChannel.type != ChannelType.STAGE && vcChannel.type != ChannelType.CATEGORY) {
            data.reply("`vc`の引数はボイスチャンネルか、ステージチャンネル、ボイスチャンネルを含むカテゴリにしてください。")
            return
        }

        val vcChannels = if (vcChannel.type == ChannelType.CATEGORY) {
            vcChannel.asCategory().channels.filter { it.type == ChannelType.VOICE || it.type == ChannelType.STAGE }
        } else {
            listOf(vcChannel)
        }

        val outStr = vcChannels.mapNotNull {
            Bot.vcConfigDao.findByVCChannelIdAndGuildId(it.idLong, event.guild!!.idLong)
                ?.let { it1 ->
                    Bot.vcConfigDao.remove(it1)
                    "${it.asMention}のVC参加通知設定を解除しました"
                }
        }.joinToString("\n")
        data.reply(outStr)

    }
}