package bot.command

class CommandPath(commandPath: String) {
    val base: String
    val category: String
    val commandName: String
    val subcommandName: String?

    init {
        val token = commandPath.split(".")
        if (token.size < 3) {
            throw IllegalArgumentException()
        }
        base = token[0]
        category = token[1]
        commandName = token[2]
        subcommandName = token.getOrNull(3)
    }

    override fun toString(): String {
        return "$base.$category.$commandName${subcommandName?.let { ".$it" } ?: ""}"
    }

    override fun hashCode(): Int {
        return ((this.base.hashCode() * 31 + this.category.hashCode()) * 31 + this.commandName.hashCode()) * 31 + this.subcommandName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is CommandPath) return false
        return this.base == other.base && this.category == other.category && this.commandName == other.commandName && this.subcommandName == other.subcommandName
    }
}