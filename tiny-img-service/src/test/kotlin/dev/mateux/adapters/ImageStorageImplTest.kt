package dev.mateux.adapters

import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("Image Storage Implementation Test")
class ImageStorageImplTest {

    lateinit var imageStorage: ImageStorageImpl
    private val rootPath = "/tmp/tiny-img-test-" + System.currentTimeMillis()
    private val image = File.createTempFile("image", ".jpeg")
    private var imagePath = ""

    @BeforeAll
    fun setUp() {
        imageStorage = ImageStorageImpl(rootPath)
    }

    @Test
    @Order(1)
    fun `should save image to the file system`() {
        // Arrange
        val imageIdentifier = "image-identifier"
        val user = "user"

        // Act
        val path = imageStorage.saveImage(imageIdentifier, image, "jpeg", user)

        // Assert
        assertTrue(File(path).exists())
        imagePath = path
    }

    @Test
    @Order(2)
    fun `should not mkdirs when folder already exists`() {
        // Arrange
        val imageIdentifier = "image-identifier"
        val user = "user"

        // Act
        val newPath = imageStorage.saveImage(imageIdentifier, image, "png", user)

        // Assert
        assertNotNull(newPath)
    }

    @Test
    @Order(3)
    fun `should remove image from the file system`() {
        // Act
        val result = imageStorage.removeImageByPath(imagePath)

        // Assert
        assertFalse(File(imagePath).exists())
        assertTrue(result)
    }
}