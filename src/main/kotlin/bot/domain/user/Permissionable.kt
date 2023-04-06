package bot.domain.user

import bot.domain.permission.PermissionManager

interface Permissionable : Savable {
    val permissions: PermissionManager
}