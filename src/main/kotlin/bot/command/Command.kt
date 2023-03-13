package bot.command

import bot.Bot
import bot.permission.CommandPermission
import bot.permission.permissionCheck
import bot.user.DiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class Command : ListenerAdapter() {

    abstract val commandName: String
    abstract val description: String
    abstract val commandPath: CommandPath
    open val commandOptions: List<OptionData> = listOf()

    open val slashCommandData: SlashCommandData
        get() = Commands.slash(commandName, "`$commandPath` : $description").addOptions(commandOptions)

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

                if (permission.runnable == true) {
                    try {
                        onSlashCommand(event, CommandEventData(user, permission, event))
                    } catch (e: Exception) {
                        val errorStr = if (Bot.isDevMode) "\n$e" else ""
                        event.hook.editOriginal("コマンドの実行中に内部エラーが発生しました。$errorStr").queue()
                    }
                } else {
                    event.hook.editOriginal("${event.user.asMention}はサーバーによって`$commandPath`の実行が禁止されています。")
                        .queue()
                }

            } catch (e: Exception) {
                event.hook.editOriginal("ロール情報が取得できないため、コマンドが実行できません。").queue()
            }
        }
    }

    abstract fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData)

}

data class CommandEventData(
    val user: DiscordUser,
    val userPermission: CommandPermission,
    val event: SlashCommandInteractionEvent
) {
    fun reply(msg: String) {
        event.hook.editOriginal(msg).queue()
    }
}