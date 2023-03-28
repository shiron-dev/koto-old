package bot

import bot.command.Command
import bot.command.core.AboutCommand
import bot.command.core.PingCommand
import bot.command.core.permission.PermissionCommand
import bot.command.util.HelloCommand
import bot.command.util.QuoteCommand
import bot.command.util.vc.VCCommand
import bot.dao.RoleDao
import bot.dao.UserDao
import bot.dao.VCConfigDao
import bot.events.MessageReceiveListener
import bot.events.VoiceChannelJoinListener
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.util.*
import java.util.jar.Manifest
import javax.security.auth.login.LoginException

const val DISCORD_MESSAGE_URL_PREFIX = "https://discord.com/channels/"

val dotenv = dotenv()

fun toEnvBoolean(value: String?): Boolean {
    return value == "True"
}

object Bot {

    lateinit var jda: JDA
    private val botToken: String? = dotenv["TOKEN"]

    val isDevMode = toEnvBoolean(dotenv["DEV_FLAG"])

    val userDao = UserDao()
    val roleDao = RoleDao()
    val vcConfigDao = VCConfigDao()

    val commands: List<Command> =
        listOf(HelloCommand(), PermissionCommand(), QuoteCommand(), PingCommand(), VCCommand(), AboutCommand())

    val stated = Date()
    val implementationVersion: String?

    init {

        val manifestStream = javaClass.getResourceAsStream("/META-INF/MANIFEST.MF")
        val manifest = Manifest(manifestStream)
        implementationVersion = manifest.mainAttributes.getValue("Implementation-Version")

        try {
            jda = JDABuilder.createDefault(
                botToken,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.MESSAGE_CONTENT
            )
                .setRawEventsEnabled(true)
                .setActivity(Activity.playing(dotenv["ACTIVITY"] ?: "Koto | /help"))
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
            // 全削除
            jda.retrieveCommands().queue {
                for (cmd in it) {
                    jda.deleteCommandById(cmd.id).queue()
                }
            }

            jda.updateCommands().addCommands(commands.map { it.slashCommandData }).queue()
            val guild = dotenv["DEV_GUILD"]?.let { jda.getGuildById(it) }
            for (cmd in commands) {
                jda.addEventListener(cmd)
            }
            guild?.updateCommands()
                ?.addCommands(commands.map { it.slashCommandData })?.queue()

            dotenv["DEV_CHANNEL"]?.let { guild?.getTextChannelById(it)?.sendMessage("Started")?.queue() }
        } else {
            // 本番モード
            for (cmd in commands) {
                jda.addEventListener(cmd)
            }
            jda.updateCommands().addCommands(commands.map { it.slashCommandData }).queue()
        }
        jda.addEventListener(MessageReceiveListener())
        jda.addEventListener(VoiceChannelJoinListener())
    }
}