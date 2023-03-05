package bot.command

import bot.Bot
import bot.user.DiscordUser
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class Command : ListenerAdapter() {

    abstract val commandName: String
    abstract val description: String
    abstract val commandPath: CommandPath

    val slashCommandData: SlashCommandData
        get() = Commands.slash(commandName, description)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == commandName) {
            if (event.guild == null) {
                event.reply("サーバーの情報を取得できないため、コマンドが実行できません。").setEphemeral(true).queue()
                return
            }

            event.deferReply().setEphemeral(true).queue()

            val user = Bot.userDao.findByDiscordUserIdAndDiscordGuildId(event.user.idLong, event.guild!!.idLong)
                ?: run {
                    val u = DiscordUser()
                    u.discordUserId = event.user.idLong
                    u.discordGuildId = event.guild!!.idLong
                    u.save()
                    u
                }

            if (user.userPermissions[commandPath].runnable) {
                onSlashCommand(event, user)
            } else {
                onNotPermission(event, user)
            }
        }
    }

    abstract fun onSlashCommand(event: SlashCommandInteractionEvent, user: DiscordUser)

    @Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
    fun onNotPermission(event: SlashCommandInteractionEvent, user: DiscordUser) {
        event.hook.editOriginal("${event.user.asMention}には`$commandPath`を実行する権限がありません。").queue()
    }
}

class CommandPath(commandPath: String) {
    val base: String
    val category: String
    val commandName: String

    init {
        val token = commandPath.split(".")
        base = token[0]
        category = token[1]
        commandName = token[2]
    }

    override fun toString(): String {
        return "$base.$category.$commandName"
    }

    override fun hashCode(): Int {
        return (this.base.hashCode() * 31 + this.category.hashCode()) * 31 + this.commandName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is CommandPath) return false
        return this.base == other.base && this.category == other.category && this.commandName == other.commandName
    }
}