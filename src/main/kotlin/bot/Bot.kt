package bot

import bot.command.Command
import bot.command.core.permission.PermissionCommand
import bot.command.util.HelloCommand
import bot.command.util.QuoteCommand
import bot.events.MessageReceiveListener
import bot.user.RoleDao
import bot.user.UserDao
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import javax.security.auth.login.LoginException

const val DISCORD_MESSAGE_URL_PREFIX = "https://discord.com/channels/"

val dotenv = dotenv()

fun toEnvBoolean(value: String): Boolean {
    return value == "True"
}

object Bot {

    lateinit var jda: JDA
    private val botToken: String = dotenv["TOKEN"]

    val isDevMode = toEnvBoolean(dotenv["DEV_FLAG"])

    val userDao = UserDao()
    val roleDao = RoleDao()

    val commands: List<Command> = listOf(HelloCommand(), PermissionCommand(), QuoteCommand())

    init {
        try {
            jda = JDABuilder.createDefault(
                botToken,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
            )
                .setRawEventsEnabled(true)
                .setActivity(Activity.playing("開発中"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build()

            jda.awaitReady()
        } catch (e: LoginException) {
            e.printStackTrace()
        }
    }

    fun start() {
        if (isDevMode) {
            // 開発モード
            val guild = jda.getGuildById(dotenv["DEV_GUILD"])
            for (cmd in commands) {
                jda.addEventListener(cmd)
            }
            guild?.updateCommands()
                ?.addCommands(commands.map { it.slashCommandData })?.queue()

            guild?.getTextChannelById(dotenv["DEV_CHANNEL"])?.sendMessage("Started")?.queue()
        } else {
            // 本番モード
        }
        jda.addEventListener(MessageReceiveListener())
    }
}