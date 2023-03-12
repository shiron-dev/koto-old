package bot.command.core.permission

import bot.command.CommandEventData
import bot.command.Subcommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SetCommand : Subcommand() {

    override val subcommandName = "set"
    override val description = "権限を設定します。"

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        event.hook.editOriginal("セットコマンド").queue()
    }

}