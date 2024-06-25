package dev.mateux.adapters

import dev.mateux.domain.Image
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp

@Entity
@Table(name = "images")
open class ImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, columnDefinition = "SERIAL PRIMARY KEY NOT NULL")
    var id: Int? = 0,
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL", name = "image_id")
    var publicId: String? = "",
    @Column(unique = true, nullable = false, length = 255, columnDefinition = "VARCHAR(255) UNIQUE NOT NULL")
    var path: String? = "",
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL UNIQUE", name = "user_id")
    var userId: String? = "",
    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", name = "created_at")
    var createdAt: Timestamp? = null,
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP", name = "updated_at")
    var updatedAt: Timestamp? = null
) {
    fun toDomain(): Image {
        val id = id ?: throw IllegalStateException("Image ID cannot be null")
        val publicId = publicId ?: throw IllegalStateException("Image public ID cannot be null")
        val path = path ?: throw IllegalStateException("Image path cannot be null")
        val userId = userId ?: throw IllegalStateException("Image user ID cannot be null")

        return Image(id, publicId, path, userId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}