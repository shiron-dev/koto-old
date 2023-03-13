package bot.user

import bot.permission.PermissionManager

interface Permissionable : Savable {
    val permissions: PermissionManager
}