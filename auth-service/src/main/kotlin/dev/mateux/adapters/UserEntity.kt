package dev.mateux.adapters

import dev.mateux.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import java.util.UUID

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
        val id = id
        val username = username
        val email = email

        if (id == null || username == null || email == null) throw IllegalStateException("UserEntity is not valid")

        return User(id, username, email)
    }
}