package dev.mateux.adapters

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
    import org.junit.jupiter.api.Test

@DisplayName("User Entity Test")
class UserEntityTest {
    @Test
    fun `should create a user entity without id`() {
        // Arrange
        val userEntity = UserEntity.withoutId("user", "user1@mail.com", "12345678", "salt")

        // Act
        val username = userEntity.username

        // Assert
        assertEquals("user", username)
        assertNull(userEntity.id)
    }

    @Test
    fun `should throw an exception when try to convert an invalid user entity to domain`() {
        // Arrange
        val userEntity1 = UserEntity.test(id = null)
        val userEntity2 = UserEntity.test(username = null)
        val userEntity3 = UserEntity.test(email = null)
        val userEntity4 = UserEntity.test(publicId = null)

        // Act and Assert
        assertThrows(IllegalStateException::class.java) { userEntity1.toDomain() }
        assertThrows(IllegalStateException::class.java) { userEntity2.toDomain() }
        assertThrows(IllegalStateException::class.java) { userEntity3.toDomain() }
        assertThrows(IllegalStateException::class.java) { userEntity4.toDomain() }
    }

    @Test
    fun `should allow a user entity to be create without any parameter`() {
        // Arrange
        val userEntity = UserEntity()

        // Act
        val id = userEntity.id
        val username = userEntity.username
        val email = userEntity.email

        // Assert
        assertEquals(0, id)
        assertEquals("", username)
        assertEquals("", email)
        assertEquals("", userEntity.password)
        assertEquals("", userEntity.salt)
        assertEquals("", userEntity.publicId)
        assertNull(userEntity.createdAt)
    }

    @Test
    fun `should return true when two user entities contain same id`() {
        // Arrange
        val userEntity1 = UserEntity.test(id = 1)
        val userEntity2 = UserEntity.test(id = 1)

        // Act
        val result = userEntity1 == userEntity2

        // Assert
        assertTrue(result)
    }

    @Test
    fun `should return false when two user entities contain different id`() {
        // Arrange
        val userEntity1 = UserEntity.test(id = 1)
        val userEntity2 = UserEntity.test(id = 2)

        // Act
        val result = userEntity1 == userEntity2

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return false when comparing different types`() {
        // Arrange
        val userEntity = UserEntity.test()

        // Act
        val result = userEntity.equals("")

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return false when comparing with null`() {
        // Arrange
        val userEntity = UserEntity.test()

        // Act
        val result = userEntity.equals(null)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should return 0 when getting hash code from user entity without id`() {
        // Arrange
        val userEntity = UserEntity.test(id = null)

        // Act
        val result = userEntity.hashCode()

        // Assert
        assertEquals(0, result)
    }

    @Test
    fun `should return 1 when getting hash code from user entity with id 1`() {
        // Arrange
        val userEntity = UserEntity.test(id = 1)

        // Act
        val result = userEntity.hashCode()

        // Assert
        assertEquals(1, result)
    }
}