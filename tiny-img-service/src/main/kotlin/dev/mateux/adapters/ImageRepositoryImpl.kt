package dev.mateux.adapters

import dev.mateux.domain.Image
import dev.mateux.ports.ImageRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager

@ApplicationScoped
class ImageRepositoryImpl(
    @Inject private var entityManager: EntityManager
): ImageRepository {
    override fun storeImage(imageEntity: ImageEntity): Image {
        return try {
            entityManager.persist(imageEntity)
            imageEntity.toDomain()
        } catch (e: Exception) {
            throw e
        }
    }
}