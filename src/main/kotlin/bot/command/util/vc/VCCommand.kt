package bot.command.util.vc

import bot.command.CommandPath
import bot.command.SubcommandableCommand

class VCCommand : SubcommandableCommand() {
    override val commandName = "vc"
    override val description = "VCの参加通知"
    override val commandPath = CommandPath.fromString("koto.util.vc")
    override val subcommands = listOf(VCSetCommand(), VCListCommand(), VCDeleteCommand())

    init {
        for (sub in subcommands) {
            sub.parentsPath = commandPath
        }
    }
}