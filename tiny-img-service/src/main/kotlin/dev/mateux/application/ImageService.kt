package dev.mateux.application

import dev.mateux.adapters.ImageEntity
import dev.mateux.application.dto.OptimizationOptions
import dev.mateux.application.dto.QueuePayload
import dev.mateux.domain.User
import dev.mateux.ports.ImageRepository
import dev.mateux.ports.ImageStorage
import dev.mateux.ports.MessageQueue
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response.Status
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.io.File
import java.util.*

@ApplicationScoped
class ImageService(
    @Inject private var imageRepository: ImageRepository,
    @Inject private var imageStorage: ImageStorage,
    @Inject private var messageQueue: MessageQueue,
    @ConfigProperty(name = "file.max.size", defaultValue = "5242880") private var maxSize: String,
    @ConfigProperty(name = "file.allowed.types", defaultValue = "image/png,image/jpeg,image/jpg,image/avif,image/webp") private var allowedTypes: String,
) {
    private val allowedOptimizationTypes = allowedTypes.split(",").map { it.split("/").last() }

    @Transactional(rollbackOn = [Exception::class])
    fun uploadImage(image: FileUpload, user: User): String {
        if (image.uploadedFile().toFile().length() > maxSize.toLong()) {
            throw WebApplicationException("File too large", Status.REQUEST_ENTITY_TOO_LARGE.statusCode)
        }

        if (!allowedTypes.contains(image.contentType())) {
            throw WebApplicationException("File type not allowed", Status.UNSUPPORTED_MEDIA_TYPE.statusCode)
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
                    throw WebApplicationException("Error removing image", Status.INTERNAL_SERVER_ERROR.statusCode)
                }

                throw WebApplicationException("Error saving image", Status.INTERNAL_SERVER_ERROR.statusCode)
            }
        }

        throw WebApplicationException("Error saving image", Status.INTERNAL_SERVER_ERROR.statusCode)
    }

    fun getImage(imageId: String): File {
        val image = imageRepository.getImageByPublicId(imageId) ?: throw WebApplicationException("Image not found", Status.NOT_FOUND.statusCode)

        return File(image.path)
    }

    fun getChildrenImages(parentId: String): List<String> {
        return imageRepository.getChildrenImages(parentId).map { it.publicId }
    }

    @Transactional(rollbackOn = [Exception::class])
    fun optimizeImage(imageId: String, options: OptimizationOptions): Boolean {
        if (!allowedOptimizationTypes.contains(options.format)) {
            throw WebApplicationException("Format not allowed", Status.UNSUPPORTED_MEDIA_TYPE.statusCode)
        }

        if (options.quality < 1 || options.quality > 100) {
            throw WebApplicationException("Quality must be between 1 and 100", Status.BAD_REQUEST.statusCode)
        }

        if (options.size < 1 || options.size > 100) {
            throw WebApplicationException("Size must be between 1% and 100%", Status.BAD_REQUEST.statusCode)
        }

        val image = imageRepository.getImageByPublicId(imageId) ?: throw WebApplicationException("Image not found", Status.NOT_FOUND.statusCode)

        val uuid = UUID.randomUUID().toString()
        val basePath = image.path.substringBeforeLast("/")
        val optimizedImage = imageRepository.storeImage(ImageEntity(
            publicId = uuid,
            path = "$basePath/$uuid.${options.format}",
            userId = image.userId,
            parentId = imageId
        ))

        return messageQueue.sendImage(QueuePayload(
            imageId = optimizedImage.publicId,
            user = optimizedImage.userId,
            originalImagePath = image.path,
            format = options.format,
            quality = options.quality,
            size = options.size
        ))
    }

    fun getMyImages(user: User): List<String> {
        return imageRepository.getImagesByUserId(user.publicId).map { it.publicId }
    }

    @Transactional(rollbackOn = [Exception::class])
    fun removeImage(imageId: String): Boolean {
        val image = imageRepository.getImageByPublicId(imageId) ?: throw WebApplicationException("Image not found", Status.NOT_FOUND.statusCode)

        if (imageRepository.removeImage(imageId)) {
            return imageStorage.removeImageByPath(image.path)
        }

        return false
    }
}