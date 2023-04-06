package bot.command.util.vc

import bot.Bot
import bot.domain.user.Savable
import jakarta.persistence.*

@Entity
@Table(name = "vc_configs")
data class VCConfig(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,

    @Column(name = "vc_channel_id")
    var vcChannelId: Long,

    @Column(name = "text_channel_id")
    var textChannelId: Long,

    @Column(name = "guild_id")
    var guildId: Long,
) : Savable {
    override fun save() {
        Bot.vcConfigDao.save(this)
    }
}