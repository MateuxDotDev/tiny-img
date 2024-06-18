package dev.mateux.adapters

import dev.mateux.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.*

@Entity
@Table(name = "users")
open class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, columnDefinition = "SERIAL PRIMARY KEY NOT NULL")
    var id: Int? = 0,
    @Column(unique = true, nullable = false, length = 255, columnDefinition = "VARCHAR(255) UNIQUE NOT NULL")
    var username: String? = "",
    @Column(unique = true, nullable = false, length = 255, columnDefinition = "VARCHAR(255) UNIQUE NOT NULL CHECK (email ~* '^.+@.+\$')")
    var email: String? = "",
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL")
    var password: String? = "",
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL")
    var salt: String? = "",
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL", name = "public_id")
    var publicId: String? = "",
    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", name = "created_at")
    var createdAt: Timestamp? = null,
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", name = "updated_at")
    var updatedAt: Timestamp? = null
) {
    companion object {
        fun withoutId(username: String, email: String, password: String, salt: String): UserEntity {
            return UserEntity(null, username, email, password, salt, UUID.randomUUID().toString())
        }

        fun test(
            id: Int? = 1,
            username: String? = "user",
            email: String? = "user@mail.com",
            password: String? = "12345678",
            salt: String? = "salt",
            publicId: String? = "1"
        ): UserEntity {
            return UserEntity(id, username, email, password, salt, publicId)
        }
    }

    fun toDomain(): User {
        val id = id ?: throw IllegalStateException("Missing id in UserEntity")
        val username = username ?: throw IllegalStateException("Missing username in UserEntity")
        val email = email ?: throw IllegalStateException("Missing email in UserEntity")
        val publicId = publicId ?: throw IllegalStateException("Missing publicId in UserEntity")

        return User(id, username, email, publicId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}