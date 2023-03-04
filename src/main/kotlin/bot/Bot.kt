package bot

import bot.command.hello.HelloCommand
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

class Bot {

    private lateinit var jda: JDA
    private val botToken: String = dotenv["TOKEN"]

    init {
        try {
            jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES)
                .setRawEventsEnabled(true)
                .setActivity(Activity.playing("開発中"))
                .build()

            jda.awaitReady()
            onReady()
        } catch (e: LoginException) {
            e.printStackTrace()
        }
    }

    private fun onReady() {
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