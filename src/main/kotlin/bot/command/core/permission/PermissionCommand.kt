package bot.command.core.permission

import bot.command.CommandPath
import bot.command.Subcommand
import bot.command.SubcommandableCommand

class PermissionCommand : SubcommandableCommand() {

    override val commandName = "permission"
    override val description = "権限を表示、設定し管理することができます。"
    override val commandPath = CommandPath("koto.core.permission")

    override val subcommands = listOf<Subcommand>(SetCommand(), ListCommand(), CheckUserCommand())

    init {
        for (sub in subcommands) {
            sub.parentsPath = commandPath
        }
    }

}