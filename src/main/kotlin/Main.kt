import bot.command.CommandPath
import bot.user.DiscordUser
import bot.user.UserDao


fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")

    val userDao = UserDao()

    // Create and save a new user
    val newUser = DiscordUser()
    newUser.userName = "test user"
    newUser.discordUserId = "test_user#0000"
    newUser.userPermissions[CommandPath("koto.util.hello")].runnable = false
    userDao.save(newUser)
    println("New user saved: ${newUser.discordUserId}")
    println("permission:: ${newUser.userPermissions[CommandPath(("koto.util.hello"))]}")

    // Get user by ID
    val userId = newUser.id
    val retrievedUser = userDao.getById(userId)
    println("Retrieved user: ${retrievedUser?.discordUserId}")

    // Get all users
    val allUsers = userDao.getAll()
    println("All users: ${allUsers.map { it.discordUserId }}")

    // Find user by name
    val discordId = "test_user#0000"
    val user = userDao.findByDiscordUserId(discordId)
    if (user != null) {
        println("User with name $discordId: $user")
    } else {
        println("User with name $discordId not found")
    }

    // Bot()
}
