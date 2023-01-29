import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import javax.security.auth.login.LoginException
import io.github.cdimascio.dotenv.dotenv


class Bot {

    private lateinit var jda: JDA
    private val botToken: String

    init {
        val dotenv = dotenv()
        botToken = dotenv["TOKEN"]
        try {
            jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES)
                .setRawEventsEnabled(true)
                .setActivity(Activity.playing("開発中"))
                .build()
        } catch (e: LoginException) {
            e.printStackTrace()
        }
    }
}

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    Bot()
}
