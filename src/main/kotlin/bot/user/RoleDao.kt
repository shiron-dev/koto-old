package bot.user

import bot.Bot
import hibernate.HibernateUtil

class RoleDao {

    fun createSave(role: DiscordRole) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.persist(role)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun save(role: DiscordRole) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.merge(role)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun findByDiscordRoleIdAndDiscordGuildIdOrMake(roleId: Long, guildId: Long): DiscordRole {
        return getListedRolesOrMake(listOf(roleId), guildId).first()
    }

    fun getListedRolesOrMake(roleIds: List<Long>, guildId: Long): List<DiscordRole> {
        val session = HibernateUtil.getSession()
        val query = session.createQuery(
            "FROM DiscordRole WHERE roleId in :roleIds AND guildId = :guildId",
            DiscordRole::class.java
        )
        query.setParameter("roleIds", roleIds)
        query.setParameter("guildId", guildId)
        val roles = query.resultList
        HibernateUtil.closeSession()
        return roleIds.map {
            roles.find { it2 -> it2.roleId == it } ?: run {
                val r = DiscordRole(roleId = it, guildId = guildId)
                Bot.roleDao.createSave(r)
                r
            }
        }
    }
}
