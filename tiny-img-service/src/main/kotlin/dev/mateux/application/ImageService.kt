package dev.mateux.application

import dev.mateux.adapters.ImageEntity
import dev.mateux.domain.User
import dev.mateux.ports.ImageRepository
import dev.mateux.ports.ImageStorage
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.WebApplicationException
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.io.File
import java.util.*

@ApplicationScoped
class ImageService(
    @Inject private var imageRepository: ImageRepository,
    @Inject private var imageStorage: ImageStorage,
    @ConfigProperty(name = "file.max.size", defaultValue = "5242880") private var maxSize: String,
    @ConfigProperty(name = "file.allowed.types", defaultValue = "image/png,image/jpeg,image/jpg,image/avif,image/webp") private var allowedTypes: String
) {
    @Transactional(rollbackOn = [Exception::class])
    fun uploadImage(image: FileUpload, user: User): String {
        if (image.uploadedFile().toFile().length() > maxSize.toLong()) {
            throw WebApplicationException("File too large", 413)
        }

        if (!allowedTypes.contains(image.contentType())) {
            throw WebApplicationException("File type not allowed", 415)
        }
        val extension = image.contentType().split("/").last()

        val imageIdentifier = UUID.randomUUID().toString()
        val imagePath = imageStorage.saveImage(imageIdentifier, image.uploadedFile().toFile(), extension, user.publicId)

        if (imagePath.isNotEmpty()) {
            try {
                return imageRepository.storeImage(ImageEntity(
                    publicId = imageIdentifier,
                    path = imagePath,
                    userId = user.publicId
                    )).publicId
            } catch (e: Exception) {
                if (!imageStorage.removeImageByPath(imagePath)) {
                    throw WebApplicationException("Error removing image", 500)
                }

                throw WebApplicationException("Error saving image", 500)
            }
        }

        throw WebApplicationException("Error saving image", 500)
    }

    fun getImage(imageId: String): File {
        val image = imageRepository.getImageByPublicId(imageId) ?: throw WebApplicationException("Image not found", 404)

        return File(image.path)
    }

    fun getChildrenImages(parentId: String): List<String> {
        return imageRepository.getChildrenImages(parentId).map { it.publicId }
    }
}