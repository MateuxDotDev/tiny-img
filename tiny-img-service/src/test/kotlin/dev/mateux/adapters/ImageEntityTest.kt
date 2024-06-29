package dev.mateux.adapters

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Image Entity")
class ImageEntityTest {
    @Test
    fun `should throw an exception when id is null while converting to domain`() {
        // Arrange
        val imageEntity = ImageEntity(id = null, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val exception = assertThrows(IllegalStateException::class.java) {
            imageEntity.toDomain()
        }

        // Assert
        assertEquals("Image ID cannot be null", exception.message)
    }

    @Test
    fun `should throw an exception when public id is null while converting to domain`() {
        // Arrange
        val imageEntity = ImageEntity(id = 1, publicId = null, path = "path", userId = "userId")

        // Act
        val exception = assertThrows(IllegalStateException::class.java) {
            imageEntity.toDomain()
        }

        // Assert
        assertEquals("Image public ID cannot be null", exception.message)
    }

    @Test
    fun `should throw an exception when path is null while converting to domain`() {
        // Arrange
        val imageEntity = ImageEntity(id = 1, publicId = "publicId", path = null, userId = "userId")

        // Act
        val exception = assertThrows(IllegalStateException::class.java) {
            imageEntity.toDomain()
        }

        // Assert
        assertEquals("Image path cannot be null", exception.message)
    }

    @Test
    fun `should throw an exception when user id is null while converting to domain`() {
        // Arrange
        val imageEntity = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = null)

        // Act
        val exception = assertThrows(IllegalStateException::class.java) {
            imageEntity.toDomain()
        }

        // Assert
        assertEquals("Image user ID cannot be null", exception.message)
    }

    @Test
    fun `should return false when comparing two different entities`() {
        // Arrange
        val imageEntity1 = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")
        val imageEntity2 = ImageEntity(id = 2, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val result = imageEntity1 == imageEntity2

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return true when comparing two equal entities`() {
        // Arrange
        val imageEntity1 = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")
        val imageEntity2 = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val result = imageEntity1 == imageEntity2

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return the same hash code for two equal entities`() {
        // Arrange
        val imageEntity1 = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")
        val imageEntity2 = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val result = imageEntity1.hashCode() == imageEntity2.hashCode()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return false if comparing two different classes`() {
        // Arrange
        val imageEntity = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val result = imageEntity.equals("")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return false if comparing with null`() {
        // Arrange
        val imageEntity = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val result = imageEntity.equals(null)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return true if comparing same instance`() {
        // Arrange
        val imageEntity = ImageEntity(id = 1, publicId = "publicId", path = "path", userId = "userId")

        // Act & Assert
        assertTrue(imageEntity.equals(imageEntity))
    }

    @Test
    fun `should return 0 as hash code when id is null`() {
        // Arrange
        val imageEntity = ImageEntity(id = null, publicId = "publicId", path = "path", userId = "userId")

        // Act
        val result = imageEntity.hashCode()

        // Assert
        assertEquals(0, result)
    }
}