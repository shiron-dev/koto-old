package bot.domain.permission

import bot.Bot
import bot.command.CommandPath

class DefaultPermissions {

    companion object {
        val allFalse = CommandPermission(runnable = false)

        fun getDefaultPermission(commandPath: CommandPath): CommandPermission? {
            return when (commandPath) {
                CommandPath.fromString("koto.util.hello") -> CommandPermission(runnable = true)
                CommandPath.fromString("koto.core.permission") -> CommandPermission(runnable = false)
                CommandPath.fromString("koto.util.quote") -> CommandPermission(runnable = true)
                CommandPath.fromString("koto.core.ping") -> CommandPermission(runnable = true)
                CommandPath.fromString("koto.util.vc") -> CommandPermission(runnable = false)
                CommandPath.fromString("koto.core.about") -> CommandPermission(runnable = true)
                CommandPath.fromString("koto.util.dice") -> CommandPermission(runnable = true)
                CommandPath.fromString("koto.util.read") -> CommandPermission(runnable = true)
                CommandPath.fromString("koto.core.leave") -> CommandPermission(runnable = true)
                else -> {
                    null
                }
            }
        }

        fun getDefaultPermissionManager(): PermissionManager {
            val pm = PermissionManager()
            for (cmd in Bot.commands) {
                cmd.commandPath ?: continue
                pm[cmd.commandPath!!] = getDefaultPermission(cmd.commandPath!!) ?: allFalse
            }
            return pm
        }
    }
}