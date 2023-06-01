package bot.command

data class CommandPath(
    val base: String,
    val category: String,
    val commandName: String,
    val subcommandName: String?,
) {

    companion object {
        fun fromString(commandPath: String): CommandPath? {
            val token = commandPath.split(".")
            if (token.size < 3 || token.size > 4) {
                return null
            }

            return CommandPath(token[0], token[1], token[2], token.getOrNull(3))
        }
    }

    override fun toString(): String {
        return "$base.$category.$commandName${subcommandName?.let { ".$it" } ?: ""}"
    }

}