package user

import jakarta.persistence.*

@Entity(name="User")
@Table(name="users")
class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0


    @Column(name = "name", length = 32, nullable = true)
    var name: String? = null
}
