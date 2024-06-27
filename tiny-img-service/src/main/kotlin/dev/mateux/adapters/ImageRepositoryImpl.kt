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

    override fun addChildImage(child: ImageEntity): Image {
        return try {
            entityManager.persist(child)
            child.toDomain()
        } catch (e: Exception) {
            throw e
        }
    }
}