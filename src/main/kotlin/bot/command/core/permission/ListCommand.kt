package bot.command.core.permission

import bot.Bot
import bot.command.CommandEventData
import bot.command.Subcommand
import bot.domain.permission.DefaultPermissions
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class ListCommand : Subcommand() {

    override val subcommandName = "list"
    override val description = "権限を表示します。"

    override val subcommandOptions =
        listOf(OptionData(OptionType.USER, "user", "ユーザー"), OptionData(OptionType.ROLE, "role", "ロール"))

    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val user = event.getOption("user")?.asUser
        val role = event.getOption("role")?.asRole

        if (user != null && role != null) {
            data.reply("`user`または`role`のどちらか一方のみを指定してください。")
            return
        }

        val target = user ?: role
        val permissionManager = user?.let {
            Bot.userDao.findByDiscordUserIdAndDiscordGuildIdOrMake(
                it.idLong,
                event.guild!!.idLong
            ).permissions
        } ?: role?.let {
            Bot.roleDao.findByDiscordRoleIdAndDiscordGuildIdOrMake(
                it.idLong,
                event.guild!!.idLong
            ).permissions
        } ?: DefaultPermissions.getDefaultPermissionManager()

        val permissionStr =
            Bot.commands.associate { it.commandPath to permissionManager[it.commandPath] }.entries
                .joinToString(separator = "\n") {
                    "${if (it.value.runnable == true) ":o:" else if (it.value.runnable == false) ":x:" else ":arrow_backward:"}`${it.key}`"
                }
        data.reply("${target?.asMention ?: "Botデフォルトの権限(開発者設定)"}に設定された権限一覧\n$permissionStr\n:o:許可、:x:禁止、:arrow_backward:未設定(ユーザーに設定された別のロール等の権限に従う)")
    }

}