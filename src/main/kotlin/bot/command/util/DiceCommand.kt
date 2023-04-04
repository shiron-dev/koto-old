package bot.command.util

import bot.command.Command
import bot.command.CommandEventData
import bot.command.CommandPath
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class DiceCommand: Command() {
    override val commandName = "dice"
    override val description = "ダイスロールを行います。デフォルトで結果が周囲に表示されます。"
    override val commandPath = CommandPath("koto.util.dice")
    override val commandOptions = listOf(OptionData(OptionType.STRING, "dice", "ex. 1d6, 2d3 e.t.c.", true))

    override val sharedDefault = true

    override fun onSlashCommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val dice = event.getOption("dice")?.asString ?: run {
            data.reply("引数`dice`を指定してください。")
            return
        }

        val diceRegex = Regex("""(\d+)d(\d+)""")
        val matchResult = diceRegex.matchEntire(dice) ?: run {
            data.reply("引数`dice`の形式が不正です。")
            return
        }

        val (count, max) = matchResult.destructured
        val countInt = count.toIntOrNull() ?: run {
            data.reply("引数`dice`の形式が不正です。")
            return
        }
        val maxInt = max.toIntOrNull() ?: run {
            data.reply("引数`dice`の形式が不正です。")
            return
        }

        val result = (1..countInt).map { (1..maxInt).random() }
        data.reply("`${dice}`のダイスロール結果\n${result.joinToString(", ")}\n合計: ${result.sum()}")
    }
}