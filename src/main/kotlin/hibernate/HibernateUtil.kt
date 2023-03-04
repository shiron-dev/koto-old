package hibernate

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration

class HibernateUtil private constructor() {
    companion object {
        private val sessionFactory: SessionFactory = buildSessionFactory()

        private fun buildSessionFactory(): SessionFactory {
            val configuration = Configuration().configure()
            return configuration.buildSessionFactory()
        }

        private val threadLocalSession = ThreadLocal<Session>()

        fun getSession(): Session {
            var session = threadLocalSession.get()
            if (session == null) {
                session = sessionFactory.openSession()
                threadLocalSession.set(session)
            }
            return session
        }

        fun closeSession() {
            val session = threadLocalSession.get()
            if (session != null) {
                session.close()
                threadLocalSession.remove()
            }
        }
    }
}