package bot

import bot.command.Command
import bot.command.core.permission.PermissionCommand
import bot.command.util.HelloCommand
import bot.user.RoleDao
import bot.user.UserDao
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import javax.security.auth.login.LoginException


val dotenv = dotenv()

fun toEnvBoolean(value: String): Boolean {
    return value == "True"
}

object Bot {

    lateinit var jda: JDA
    private val botToken: String = dotenv["TOKEN"]

    val userDao = UserDao()
    val roleDao = RoleDao()

    val commands: List<Command> = listOf(HelloCommand(), PermissionCommand())

    init {
        try {
            jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES)
                .setRawEventsEnabled(true)
                .setActivity(Activity.playing("開発中"))
                .build()

            jda.awaitReady()
        } catch (e: LoginException) {
            e.printStackTrace()
        }
    }

    fun start() {
//        jda.getGuildById(1050251870649724999)?.getTextChannelById(1074241417158873118)
//            ?.sendMessage("https://www.youtube.com/watch?v=OVuYIMa5XBw")?.queue()
        if (toEnvBoolean(dotenv["DEV_FLAG"])) {
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
    }
}