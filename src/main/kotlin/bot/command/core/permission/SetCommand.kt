package bot.command.core.permission

import bot.Bot
import bot.command.CommandEventData
import bot.command.CommandPath
import bot.command.Subcommand
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class SetCommand : Subcommand() {

    override val subcommandName = "set"
    override val description = "権限を設定します。"

    override val subcommandOptions =
        listOf(
            OptionData(OptionType.STRING, "command", "コマンド", true),
            OptionData(OptionType.STRING, "value", "ok(実行を許可), ng(実行を禁止), def(デフォルト権限)", true),
            OptionData(OptionType.USER, "user", "ユーザー"),
            OptionData(OptionType.ROLE, "role", "ロール")
        )

    @Suppress("DuplicatedCode")
    override fun onSubcommand(event: SlashCommandInteractionEvent, data: CommandEventData) {
        val user = event.getOption("user")?.asUser
        val role = event.getOption("role")?.asRole
        if (user != null && role != null) {
            data.reply("`user`または`role`のどちらか一方のみを指定してください。")
            return
        }
        val target = user ?: role ?: run {
            data.reply("`user`または`role`のどちらか片方を指定してください。")
            return
        }
        val permissionable =
            user?.let { Bot.userDao.findByDiscordUserIdAndDiscordGuildIdOrMake(it.idLong, event.guild!!.idLong) }
                ?: role?.let { Bot.roleDao.findByDiscordRoleIdAndDiscordGuildIdOrMake(it.idLong, event.guild!!.idLong) }
                ?: run {
                    data.reply("内部エラーです。Permissionableオブジェクトが取得できません。")
                    return
                }

        val cmd = event.getOption("command")?.asString ?: run {
            data.reply("`command`を指定してください。")
            return
        }
        val value = when (event.getOption("value")?.asString?.lowercase()) {
            "ok" -> true
            "ng" -> false
            "def" -> null
            else -> {
                data.reply("`value`には`ok`(実行を許可), `ng`(実行を禁止), `def`(デフォルト権限)のどれかを指定してください。")
                return
            }
        }

        val errorStr = "`cmd`にはコマンドの実行名(スラッシュコマンドを実行するときの名前`/<実行名>`やCommandPathを指定できます。\n" +
                "各コマンドのCommandPathは`/permission list`やコマンドの説明文、`/help`などで調べることができます。"
        val cmdPath = try {
            val cp = CommandPath.fromString(cmd)
            Bot.commands.find { it.commandPath == cp } ?: run {
                data.reply("存在しないCommandPathです。\n$errorStr")
                return
            }
            cp
        } catch (e: IllegalArgumentException) {
            Bot.commands.find { it.commandName == cmd }?.commandPath ?: run {
                data.reply("`cmd`が不正な形です。\n$errorStr")
                return
            }
        } ?: run {
            data.reply("サブコマンドのパスが取得できませんでした。Botに問題が生じている可能性があります。")
            return@onSubcommand
        }
        permissionable.permissions[cmdPath].runnable = value
        permissionable.save()

        data.reply("${target.asMention}の`$cmdPath`の権限を`$value`に設定しました")
    }

}