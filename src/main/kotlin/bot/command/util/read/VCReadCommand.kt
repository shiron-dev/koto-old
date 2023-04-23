package bot.command.util.read

import bot.Bot
import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class VCReadCommand : Command() {
    override val commandName = "vcread"
    override val description = "VCでメッセージを読み上げます"
    override val commandPath = CommandPath("koto.util.read")

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val members = event.guild!!.members
        val user = members.find { event.user.idLong == it.idLong } ?: return

        val vc = user.voiceState?.channel ?: return

        Bot.vcReadMap[vc.guild.idLong] = event.messageChannel.idLong

        val audioManager = event.guild!!.audioManager

        if (audioManager.isConnected) {
            data.reply("${audioManager.connectedChannel?.asMention}にすでに参加しています。Botを退出させる場合は/leaveと入力してください。")
            return
        }

        audioManager.openAudioConnection(vc)
        data.reply("${vc.asMention}に接続しました。")
    }
}