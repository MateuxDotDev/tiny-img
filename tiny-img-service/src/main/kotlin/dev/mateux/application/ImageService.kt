package dev.mateux.application

import dev.mateux.adapters.ImageEntity
import dev.mateux.domain.User
import dev.mateux.ports.ImageRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.WebApplicationException
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.resteasy.reactive.multipart.FileUpload

@ApplicationScoped
class ImageService(
    @Inject private var imageRepository: ImageRepository,
    @ConfigProperty(name = "file.root.path", defaultValue = "/tmp/tiny-img") private var rootPath: String,
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
        val imagePath = saveImage(imageIdentifier, image.uploadedFile().toFile(), extension, user.publicId)

        if (imagePath.isNotEmpty()) {
            try {
                return imageRepository.storeImage(ImageEntity(
                    publicId = imageIdentifier,
                    path = "$imagePath.$extension",
                    userId = user.publicId
                    )).publicId
            } catch (e: Exception) {
                if (removeImageByPath(imagePath)) {
                    throw WebApplicationException("Error removing image", 500)
                }

                throw WebApplicationException("Error saving image", 500)
            }
        }

        throw WebApplicationException("Error saving image", 500)
    }

    private fun saveImage(imageIdentifier: String, image: File, extension: String, user: String): String {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val path = "$rootPath/$today/$user"
        val folder = File(path)

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val newFile = File("$path/${imageIdentifier}/${imageIdentifier}.$extension")
        image.copyTo(newFile)

        return newFile.absolutePath
    }

    private fun removeImageByPath(path: String): Boolean {
        return File(path).delete()
    }
}