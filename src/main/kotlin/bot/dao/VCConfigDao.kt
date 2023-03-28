package bot.dao

import bot.command.util.vc.VCConfig
import hibernate.HibernateUtil
import jakarta.transaction.Transactional

class VCConfigDao {
    fun createSave(vcConfig: VCConfig) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.persist(vcConfig)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun save(vcConfig: VCConfig) {
        val session = HibernateUtil.getSession()
        val tx = session.beginTransaction()
        session.merge(vcConfig)
        tx.commit()
        HibernateUtil.closeSession()
    }

    fun getByGuildId(guildId: Long): List<VCConfig> {
        val session = HibernateUtil.getSession()
        val query = session.createQuery("FROM VCConfig WHERE guildId = :guildId", VCConfig::class.java)
        query.setParameter("guildId", guildId)
        val vcConfigs = query.resultList
        HibernateUtil.closeSession()
        return vcConfigs
    }

    fun findByVCChannelIdAndGuildId(vcChannelId: Long, guildId: Long): VCConfig? {
        val session = HibernateUtil.getSession()
        val query = session.createQuery(
            "FROM VCConfig WHERE vcChannelId = :vcChannelId AND guildId = :guildId",
            VCConfig::class.java
        )
        query.setParameter("vcChannelId", vcChannelId)
        query.setParameter("guildId", guildId)
        val vcConfig = query.setMaxResults(1).uniqueResult()
        HibernateUtil.closeSession()
        return vcConfig
    }

    @Transactional
    fun remove(vcConfig: VCConfig) {
        val session = HibernateUtil.getSession()
        val transaction = session.beginTransaction()
        session.remove(vcConfig)
        transaction.commit()
        HibernateUtil.closeSession()
    }
}