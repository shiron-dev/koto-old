package bot.command

import bot.Bot
import bot.permission.permissionCheck
import bot.user.DiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import java.io.IOException

abstract class Command : ListenerAdapter() {

    abstract val commandName: String
    abstract val description: String
    abstract val commandPath: CommandPath
    open val commandOptions: List<OptionData> = listOf()

    open val slashCommandData: SlashCommandData
        get() = Commands.slash(commandName, description).addOptions(commandOptions)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == commandName) {
            if (event.guild == null) {
                event.reply("サーバーの情報を取得できないため、コマンドが実行できません。").setEphemeral(true).queue()
                return
            }

            event.deferReply().setEphemeral(true).queue()

            val user = Bot.userDao.findByDiscordUserIdAndDiscordGuildIdOrMake(event.user.idLong, event.guild!!.idLong)

            try {
                val permission = permissionCheck(commandPath, event.user.idLong, event.guild!!.idLong, user)

                if (permission.viewable == true) {
                    if (permission.runnable == true) {
                        onSlashCommand(event, user)
                    } else {
                        event.hook.editOriginal("${event.user.asMention}はサーバーによって`$commandPath`の実行が禁止されています。")
                            .queue()
                    }
                } else {
                    event.hook.editOriginal("コマンドが見つかりません。").queue()
                }

            } catch (e: IOException) {
                event.hook.editOriginal("ロール情報が取得できません。").queue()
            }
        }
    }

    abstract fun onSlashCommand(event: SlashCommandInteractionEvent, user: DiscordUser)

}

class CommandPath(commandPath: String) {
    val base: String
    val category: String
    val commandName: String
    val subcommandName: String?

    init {
        val token = commandPath.split(".")
        base = token[0]
        category = token[1]
        commandName = token[2]
        subcommandName = token.getOrNull(3)
    }

    override fun toString(): String {
        return "$base.$category.$commandName${subcommandName?.let { ".$it" } ?: ""}"
    }

    override fun hashCode(): Int {
        return ((this.base.hashCode() * 31 + this.category.hashCode()) * 31 + this.commandName.hashCode()) * 31 + this.subcommandName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is CommandPath) return false
        return this.base == other.base && this.category == other.category && this.commandName == other.commandName && this.subcommandName == other.subcommandName
    }
}