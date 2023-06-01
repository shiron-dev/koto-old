package bot.command.core

import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class LeaveCommand : Command() {
    override val commandName = "leave"
    override val description = "BotをVCから退出させます"
    override val commandPath = CommandPath.fromString("koto.core.leave")

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val audioManager = event.guild!!.audioManager

        if (audioManager.isConnected) {
            val vc = audioManager.connectedChannel?.asMention ?: "`不明なチャンネル`"
            audioManager.closeAudioConnection()
            data.reply("${vc}から退出しました。")
        } else {
            data.reply("Botはボイスチャンネルに参加していません。")
        }
    }
}