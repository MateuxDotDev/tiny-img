package dev.mateux.adapters

import dev.mateux.ports.ImageStorage
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ApplicationScoped
class ImageStorageImpl(
    @ConfigProperty(name = "file.root.path", defaultValue = "/tmp/tiny-img") private var rootPath: String,
): ImageStorage {
    override fun saveImage(imageIdentifier: String, image: File, extension: String, user: String): String {
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

    override fun removeImageByPath(path: String): Boolean {
        return File(path).delete()
    }
}