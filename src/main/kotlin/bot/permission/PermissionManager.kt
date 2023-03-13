package bot.permission

import com.fasterxml.jackson.databind.ObjectMapper
import bot.command.CommandPath
import jakarta.persistence.*


@Entity
@Table(name = "permissions")
class PermissionManager(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int = 0,

    @Column(name = "permissions")
    @Convert(converter = PermissionsConverter::class)
    private var permissions: MutableMap<CommandPath, CommandPermission> = mutableMapOf()
) {

    operator fun get(key: CommandPath): CommandPermission {
        return CommandPermission.NullPermission.also { permissions[key] = it }
    }

    operator fun set(key: CommandPath, value: CommandPermission) {
        permissions[key] = value
    }

    fun getPermissionsMap(): Map<CommandPath, CommandPermission> {
        return permissions.toMap()
    }
}

@Converter
class PermissionsConverter : AttributeConverter<MutableMap<CommandPath, CommandPermission>, String> {
    override fun convertToDatabaseColumn(attribute: MutableMap<CommandPath, CommandPermission>?): String? {
        return ObjectMapper().writeValueAsString(
            attribute?.mapKeys { it.key.toString() }
        )
    }

    override fun convertToEntityAttribute(dbData: String?): MutableMap<CommandPath, CommandPermission> {
        if (dbData === null) {
            return mutableMapOf()
        }
        return ObjectMapper().readValue(
            dbData,
            mutableMapOf<String, CommandPermission>().javaClass
        ).mapKeys { CommandPath(it.key) }.toMutableMap()
    }
}
