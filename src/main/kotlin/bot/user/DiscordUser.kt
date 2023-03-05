package bot.user

import bot.Bot
import jakarta.persistence.*
import bot.permission.UserPermission

@Entity()
@Table(name = "users")
class DiscordUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0

    @Column(name = "discord_user_id", nullable = true)
    var discordUserId: Long? = null

    @OneToOne(cascade = [CascadeType.ALL])
    val userPermissions: UserPermission = UserPermission()

    fun save() {
        Bot.userDao.save(this)
    }
}
