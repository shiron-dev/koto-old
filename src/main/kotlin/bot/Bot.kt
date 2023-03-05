package bot

import bot.command.hello.HelloCommand
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
        if (toEnvBoolean(dotenv["DEV_FLAG"])) {
            // 開発モード
            val guild = jda.getGuildById(dotenv["DEV_GUILD"])
            val helloCommand = HelloCommand()
            jda.addEventListener(helloCommand)
            guild?.updateCommands()?.addCommands(helloCommand.slashCommandData)?.queue()
        } else {
            // 本番モード
        }
    }
}