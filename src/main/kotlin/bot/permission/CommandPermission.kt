package bot.permission

data class CommandPermission(var runnable: Boolean, var isDefault: Boolean = false) {
    companion object {
        val NullPermission = CommandPermission(runnable = false)
    }
}