package bot.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

abstract class SubcommandableCommand : Command() {

    abstract val subcommands: List<Subcommand>

    override val slashCommandData: SlashCommandData
        get() = Commands.slash(commandName, description)
            .addOptions(commandOptions).addSubcommands(subcommands.map { it.subcommandData })


    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        for (sub in subcommands) {
            if (event.subcommandName == sub.subcommandName) {
                sub.onSubcommand(event, data)
                return
            }
        }
        onNotHasSubcommand(event, data)
    }

    open fun onNotHasSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        event.hook.editOriginal("`$commandPath`にはサブコマンド`${event.subcommandName}`が存在しません。").queue()
    }
}

abstract class Subcommand {
    abstract val subcommandName: String
    abstract val description: String

    open val subcommandOptions: List<OptionData> = listOf()

    var parentsPath: CommandPath? = null

    @Suppress("unused")
    val commandPath: CommandPath?
        get() = parentsPath?.let { CommandPath("$it.$subcommandName") }

    val subcommandData: SubcommandData
        get() = SubcommandData(subcommandName, description).addOptions(subcommandOptions)

    abstract fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData)
}