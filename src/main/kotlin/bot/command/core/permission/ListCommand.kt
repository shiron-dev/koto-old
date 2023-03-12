package bot.command.core.permission

import bot.command.CommandEventData
import bot.command.Subcommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class ListCommand : Subcommand() {

    override val subcommandName = "list"
    override val description = "権限を表示します。"

    override val subcommandOptions =
        listOf(OptionData(OptionType.USER, "user", "ユーザー"), OptionData(OptionType.ROLE, "role", "ロール"))

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        event.hook.editOriginal("リストコマンド").queue()
    }

}