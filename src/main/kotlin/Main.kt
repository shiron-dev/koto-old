import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import javax.security.auth.login.LoginException

private var jda: JDA? = null
private const val BOT_TOKEN = "TOKEN"

fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    try {
        jda = JDABuilder.createDefault(BOT_TOKEN, GatewayIntent.GUILD_MESSAGES)
            .setRawEventsEnabled(true)
            .setActivity(Activity.playing("cording"))
            .build()
    } catch (e: LoginException) {
        e.printStackTrace()
    }
}
