package bot.permission

import bot.command.CommandPath

class DefaultPermissions {
    companion object {
        fun getDefaultPermission(commandPath: CommandPath): CommandPermission? {
            return when (commandPath) {
                CommandPath("koto.util.hello") -> CommandPermission(runnable = true)
                else -> {
                    null
                }
            }.also { it?.isDefault = true }
        }
    }
}