package bot.permission

data class CommandPermission(var runnable: Boolean?, var viewable: Boolean?) {
    companion object {
        val NullPermission = CommandPermission(runnable = null, viewable = null)
    }
}