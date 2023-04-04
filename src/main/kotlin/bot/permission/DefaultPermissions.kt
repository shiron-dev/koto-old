package bot.permission

import bot.Bot
import bot.command.CommandPath

class DefaultPermissions {

    companion object {
        val allFalse = CommandPermission(runnable = false)

        fun getDefaultPermission(commandPath: CommandPath): CommandPermission? {
            return when (commandPath) {
                CommandPath("koto.util.hello") -> CommandPermission(runnable = true)
                CommandPath("koto.core.permission") -> CommandPermission(runnable = false)
                CommandPath("koto.util.quote") -> CommandPermission(runnable = true)
                CommandPath("koto.core.ping") -> CommandPermission(runnable = true)
                CommandPath("koto.util.vc") -> CommandPermission(runnable = false)
                CommandPath("koto.core.about") -> CommandPermission(runnable = true)
                CommandPath("koto.util.dice") -> CommandPermission(runnable = true)
                else -> {
                    null
                }
            }
        }

        fun getDefaultPermissionManager(): PermissionManager {
            val pm = PermissionManager()
            for (cmd in Bot.commands) {
                pm[cmd.commandPath] = getDefaultPermission(cmd.commandPath) ?: allFalse
            }
            return pm
        }
    }
}