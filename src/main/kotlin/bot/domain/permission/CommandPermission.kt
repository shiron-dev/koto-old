package bot.domain.permission

data class CommandPermission(var runnable: Boolean?) {
    companion object {
        val NullPermission
            get() = CommandPermission(runnable = null)
    }
}