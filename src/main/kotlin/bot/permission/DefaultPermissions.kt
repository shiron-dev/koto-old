package bot.permission

import bot.command.CommandPath

class DefaultPermissions {

    companion object {
        val allFalse = CommandPermission(runnable = false, viewable = false)

        fun getDefaultPermission(commandPath: CommandPath): CommandPermission? {
            return when (commandPath) {
                CommandPath("koto.util.hello") -> CommandPermission(runnable = true, viewable = true)
                else -> {
                    null
                }
            }
        }
    }
}