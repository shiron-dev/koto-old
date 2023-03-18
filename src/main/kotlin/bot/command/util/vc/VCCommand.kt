package bot.command.util.vc

import bot.command.CommandPath
import bot.command.SubcommandableCommand

class VCCommand : SubcommandableCommand() {
    override val commandName = "vc"
    override val description = "VCの参加通知"
    override val commandPath = CommandPath("koto.util.vc")
    override val subcommands = listOf(VCSetCommand(), VCListCommand())
}