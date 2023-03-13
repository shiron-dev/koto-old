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

    for (permission in getRolePermissions(commandPath, userId, guildId)) {
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

fun permissionViewableChecker(commandPath: CommandPath, userId: Long, guildId: Long, user: DiscordUser): Boolean? {
    var ret = user.permissions[commandPath].viewable

    for (permission in getRolePermissions(commandPath, userId, guildId)) {
        if (ret != null) {
            return ret
        }
        ret = permission.viewable
    }

    return ret
}

private fun getRolePermissions(commandPath: CommandPath, userId: Long, guildId: Long): List<CommandPermission> {
    val roleIds = (Bot.jda.getGuildById(guildId)?.getMemberById(userId)?.roles?.toList()?.map { it.idLong }
        ?: throw IOException()) + listOf(Bot.jda.getGuildById(guildId)!!.publicRole.idLong)
    return Bot.roleDao.getListedRolesOrMake(roleIds, guildId)
        .map { it.permissions[commandPath] } + listOf(
        DefaultPermissions.getDefaultPermission(
            commandPath
        ) ?: DefaultPermissions.allFalse
    )
}
