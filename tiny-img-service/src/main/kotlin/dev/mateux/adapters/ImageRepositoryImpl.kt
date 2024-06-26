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

    override fun getImageByPublicId(publicId: String): Image? {
        return try {
            entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.publicId = :publicId", ImageEntity::class.java)
                .setParameter("publicId", publicId)
                .resultList
                .firstOrNull()
                ?.toDomain()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getChildrenImages(parentId: String): List<Image> {
        return try {
            entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.parentId = :parentId", ImageEntity::class.java)
                .setParameter("parentId", parentId)
                .resultList
                .map { it.toDomain() }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun removeImage(publicId: String): Boolean {
        return try {
            entityManager.createQuery("DELETE FROM ImageEntity i WHERE i.publicId = :publicId")
                .setParameter("publicId", publicId)
                .executeUpdate() > 0
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getImagesByUserId(userId: String): List<Image> {
        return try {
            entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.userId = :userId", ImageEntity::class.java)
                .setParameter("userId", userId)
                .resultList
                .map { it.toDomain() }
        } catch (e: Exception) {
            throw e
        }
    }
}