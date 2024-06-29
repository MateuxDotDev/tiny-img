package dev.mateux.application

import dev.mateux.domain.Image
import dev.mateux.domain.User
import dev.mateux.ports.ImageRepository
import dev.mateux.ports.ImageStorage
import dev.mateux.ports.MessageQueue
import io.quarkus.test.junit.QuarkusTest
import jakarta.ws.rs.WebApplicationException
import org.jboss.resteasy.reactive.multipart.FileUpload
import org.jboss.resteasy.reactive.server.core.multipart.DefaultFileUpload
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import java.io.File
import java.nio.file.Path

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Image Service Test")
class ImageServiceTest {
    private lateinit var imageRepository: ImageRepository
    private lateinit var imageStorage: ImageStorage
    private lateinit var messageQueue: MessageQueue
    private lateinit var imageService: ImageService
    private lateinit var fileUpload: FileUpload

    @BeforeAll
    fun setUp() {
        imageRepository = mock(ImageRepository::class.java)
        imageStorage = mock(ImageStorage::class.java)
        messageQueue = mock(MessageQueue::class.java)
        fileUpload = mock(DefaultFileUpload::class.java)
        imageService = ImageService(imageRepository, imageStorage, messageQueue, "5242880", "image/png,image/jpeg,image/jpg,image/avif,image/webp")
    }

    @BeforeEach
    fun reset() {
        reset(imageRepository, imageStorage, messageQueue, fileUpload)
    }

    @Test
    fun `should throw an exception when file is to large`() {
        // Arrange
        val user = User.test()
        mockUploadFileSize(5242880 * 2)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            imageService.uploadImage(fileUpload, user)
        }

        // Assert
        assertEquals("File too large", exception.message)
        assertEquals(413, exception.response.status)
    }

    private fun mockUploadFileSize(size: Long = 5242880L) {
        val uploadedFile = mock(Path::class.java)
        `when`(fileUpload.uploadedFile()).thenReturn(uploadedFile)
        val file = mock(File::class.java)
        `when`(uploadedFile.toFile()).thenReturn(file)
        `when`(file.length()).thenReturn(size)
    }

    @Test
    fun `should throw an exception when file type is not allowed`() {
        // Arrange
        val user = User.test()
        mockUploadFileSize()
        `when`(fileUpload.contentType()).thenReturn("image/gif")

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            imageService.uploadImage(fileUpload, user)
        }

        // Assert
        assertEquals("File type not allowed", exception.message)
        assertEquals(415, exception.response.status)
    }

    @Test
    fun `should throw an exception when image path is empty`() {
        // Arrange
        val user = User.test()
        mockUploadFileSize()
        `when`(fileUpload.contentType()).thenReturn("image/jpeg")
        `when`(imageStorage.saveImage(anyString(), anyOrNull(), anyString(), anyString())).thenReturn("")

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            imageService.uploadImage(fileUpload, user)
        }

        // Assert
        assertEquals("Error saving image", exception.message)
        assertEquals(500, exception.response.status)
    }

    @Test
    fun `should throw an exception when repository fails to store image`() {
        // Arrange
        val user = User.test()
        mockUploadFileSize()
        `when`(fileUpload.contentType()).thenReturn("image/jpeg")
        `when`(imageStorage.saveImage(anyString(), anyOrNull(), anyString(), anyString())).thenReturn("path")
        `when`(imageRepository.storeImage(anyOrNull())).thenThrow(RuntimeException())
        `when`(imageStorage.removeImageByPath(anyString())).thenReturn(true)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            imageService.uploadImage(fileUpload, user)
        }

        // Assert
        assertEquals("Error saving image", exception.message)
        assertEquals(500, exception.response.status)
    }

    @Test
    fun `should throw an exception when storage fails to remove a image after repository fails to store image`() {
        // Arrange
        val user = User.test()
        mockUploadFileSize()
        `when`(fileUpload.contentType()).thenReturn("image/jpeg")
        `when`(imageStorage.saveImage(anyString(), anyOrNull(), anyString(), anyString())).thenReturn("path")
        `when`(imageRepository.storeImage(anyOrNull())).thenThrow(RuntimeException())
        `when`(imageStorage.removeImageByPath(anyString())).thenReturn(false)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            imageService.uploadImage(fileUpload, user)
        }

        // Assert
        assertEquals("Error removing image", exception.message)
        assertEquals(500, exception.response.status)
    }

    @Test
    fun `should return false when remove image can not remove an image from repository`() {
        // Arrange
        val image = Image.test()
        `when`(imageRepository.getImageByPublicId(anyString())).thenReturn(image)
        `when`(imageRepository.removeImage(image.publicId)).thenReturn(false)

        // Act
        val result = imageService.removeImage(image.publicId)

        // Assert
        assertEquals(false, result)
    }
}
