package bot.user

import hibernate.HibernateUtil

@Suppress("unused")
class UserDao {
    fun save(user: DiscordUser) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.persist(user)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun getById(id: Long): DiscordUser? {
        val session = HibernateUtil.getSession()
        val user = session.get(DiscordUser::class.java, id)
        HibernateUtil.closeSession()
        return user
    }

    fun update(user: DiscordUser) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.merge(user)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun delete(user: DiscordUser) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.remove(user)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun getAll(): List<DiscordUser> {
        val session = HibernateUtil.getSession()
        val users = session.createQuery("FROM DiscordUser ", DiscordUser::class.java).list()
        HibernateUtil.closeSession()
        return users
    }

    fun findByDiscordUserIdAndDiscordGuildId(userId: Long, guildId: Long): DiscordUser? {
        val session = HibernateUtil.getSession()
        val query = session.createQuery(
            "FROM DiscordUser WHERE discordUserId = :userId AND discordGuildId = :guildId",
            DiscordUser::class.java
        )
        query.setParameter("userId", userId)
        query.setParameter("guildId", guildId)
        val user = query.setMaxResults(1).uniqueResult()
        HibernateUtil.closeSession()
        return user
    }
}
