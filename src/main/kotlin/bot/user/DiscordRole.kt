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

    @Column(name = "discord_role_id")
    var discordRoleId: Long,

    @Column(name = "discord_guild_id")
    var discordGuildId: Long,

    @OneToOne(cascade = [CascadeType.ALL])
    val permissions: PermissionManager = PermissionManager()

) {
    fun save() {
        Bot.roleDao.save(this)
    }
}