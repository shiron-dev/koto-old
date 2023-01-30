import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import user.User


fun main(args: Array<String>) {

    println("Program arguments: ${args.joinToString()}")


    val registry = StandardServiceRegistryBuilder()
        .configure()
        .build()
    val sessionFactory = MetadataSources(registry).buildMetadata().buildSessionFactory()

    val session = sessionFactory.openSession()

    session.beginTransaction()

    val user = User()
    user.name = "New User"
    session.persist(user)

    val query = session.createQuery("From User", User::class.java)
    val results: List<User> = query.list()
    println("number of users:" + results.size)
    for (u in results) {
        println("User:" + u + " " + u.name)
    }
    session.transaction.commit()
    session.close()

    // Bot()
}
