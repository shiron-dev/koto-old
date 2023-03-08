package bot.permission

import bot.Bot
import bot.command.CommandPath
import bot.user.DiscordUser
import java.io.IOException
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

fun permissionCheck(commandPath: CommandPath, userId: Long, guildId: Long): CommandPermission {
    return permissionCheck(
        commandPath,
        userId,
        guildId,
        Bot.userDao.findByDiscordUserIdAndDiscordGuildIdOrMake(userId, guildId)
    )
}

fun permissionCheck(commandPath: CommandPath, userId: Long, guildId: Long, user: DiscordUser): CommandPermission {
    val ret = user.permissions[commandPath]

    val roleIds = (Bot.jda.getGuildById(guildId)?.getMemberById(userId)?.roles?.toMutableList()?.map { it.idLong }
        ?: throw IOException()) + listOf(Bot.jda.getGuildById(guildId)!!.publicRole.idLong)
    val rolePermissions = Bot.roleDao.getListedRolesOrMake(roleIds, guildId)
        .map { it.permissions[commandPath] } + listOf(
        DefaultPermissions.getDefaultPermission(
            commandPath
        ) ?: DefaultPermissions.allFalse
    )
    for (permission in rolePermissions) {
        var isCompleted = true

        val properties = CommandPermission::class.memberProperties
        for (prop in properties) {
            if (prop.returnType.toString() == "kotlin.Boolean?") {
                val v = prop.get(ret) as Boolean?
                if (v == null) {
                    val value = prop.get(permission) as Boolean?
                    if (value != null) {
                        (prop as KMutableProperty<*>).setter.call(ret, value)
                    } else {
                        isCompleted = false
                    }
                }
            }
        }
        if (isCompleted) {
            break
        }
    }

    return ret
}