package bot.command.core.permission

import bot.Bot
import bot.command.CommandEventData
import bot.command.Subcommand
import bot.permission.PermissionManager
import bot.permission.permissionCheck
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class CheckUserCommand : Subcommand() {

    override val subcommandName = "checkuser"
    override val description = "ユーザーのロール等を考慮した実際の権限を表示します。"

    override val subcommandOptions =
        listOf(OptionData(OptionType.USER, "user", "ユーザー", true))

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val user = event.getOption("user")?.asUser ?: run {
            event.hook.editOriginal("引数`user`を確認できません。").queue()
            return
        }
        val permissionManager = PermissionManager()
        for (command in Bot.commands.map { it.commandPath }) {
            permissionManager[command] = permissionCheck(command, user.idLong, event.guild!!.idLong)
        }
        val permissionStr =
            permissionManager.getPermissionsMap().entries.filter { it.value.viewable == true }
                .joinToString(separator = "\n") {
                    "${if (it.value.runnable == true) ":o:" else ":x:"}`${it.key}`"
                }

        event.hook.editOriginal("${user.asMention}の実際の権限\n$permissionStr").queue()
    }

}