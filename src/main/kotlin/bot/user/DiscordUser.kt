package bot.user

import bot.Bot
import jakarta.persistence.*
import bot.permission.PermissionManager

@Entity
@Table(name = "users")
class DiscordUser(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "discord_user_id")
    var discordUserId: Long? = null,

    @Column(name = "discord_guild_id")
    var discordGuildId: Long? = null,
) : Permissionable {
    @OneToOne(cascade = [CascadeType.ALL])
    override val permissions = PermissionManager()

    override fun save() {
        Bot.userDao.save(this)
    }
}
