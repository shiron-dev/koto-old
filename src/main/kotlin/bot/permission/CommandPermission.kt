package bot.permission

data class CommandPermission(var runnable: Boolean?, var viewable: Boolean?) {
    companion object {
        val NullPermission
            get() = CommandPermission(runnable = null, viewable = null)
    }
}