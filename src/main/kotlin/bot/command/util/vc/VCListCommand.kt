package bot.command.util.vc

import bot.Bot
import bot.command.CommandEventData
import bot.command.Subcommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class VCListCommand : Subcommand() {
    override val subcommandName = "list"
    override val description = "VC通知設定しているチャンネルの一覧を表示します。"

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val vcConfigs = Bot.vcConfigDao.getByGuildId(event.guild!!.idLong)
        val vcMap = mutableMapOf<Long, MutableList<Long>>()
        for (vcConfig in vcConfigs) {
            vcMap.computeIfAbsent(vcConfig.textChannelId) { mutableListOf() }.add(vcConfig.vcChannelId)
        }

        data.reply(
            "> VC通知設定リスト\n${
                vcMap.entries.joinToString(separator = "\n") {
                    "<#${it.key}>\n${
                        it.value.joinToString(
                            separator = "\n"
                        ) { it1 -> "-> <#$it1>" }
                    }"
                }
            }"
        )
    }

}