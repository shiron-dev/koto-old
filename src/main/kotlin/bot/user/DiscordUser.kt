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

    @Column(name = "user_id")
    var userId: Long? = null,

    @Column(name = "guild_id")
    var guildId: Long? = null,
) : Permissionable {
    @OneToOne(cascade = [CascadeType.ALL])
    override val permissions = PermissionManager()

    override fun save() {
        Bot.userDao.save(this)
    }
}
