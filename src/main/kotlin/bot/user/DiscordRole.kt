package bot.user

import bot.Bot
import bot.permission.PermissionManager
import jakarta.persistence.*

@Entity
@Table(name = "roles")
class DiscordRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "role_id")
    var roleId: Long,

    @Column(name = "guild_id")
    var guildId: Long
) : Permissionable {

    @OneToOne(cascade = [CascadeType.ALL])
    override val permissions = PermissionManager()

    override fun save() {
        Bot.roleDao.save(this)
    }
}