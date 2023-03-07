package bot.user

import hibernate.HibernateUtil

class RoleDao {
    fun save(role: DiscordRole) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.persist(role)
        tx.commit()
        HibernateUtil.closeSession()
    }

    private fun findByDiscordRoleIdAndDiscordGuildId(roleId: Long, guildId: Long): DiscordRole? {
        val session = HibernateUtil.getSession()
        val query = session.createQuery(
            "FROM DiscordRole WHERE discordRoleId = :roleId AND discordGuildId = :guildId",
            DiscordRole::class.java
        )
        query.setParameter("roleId", roleId)
        query.setParameter("guildId", guildId)
        val role = query.setMaxResults(1).uniqueResult()
        HibernateUtil.closeSession()
        return role
    }

    fun findByDiscordRoleIdAndDiscordGuildIdOrMake(roleId: Long, guildId: Long): DiscordRole {
        return findByDiscordRoleIdAndDiscordGuildId(
            roleId,
            guildId
        ) ?: run {
            val r = DiscordRole(discordRoleId = roleId, discordGuildId = guildId)
            r.save()
            r
        }
    }
}
