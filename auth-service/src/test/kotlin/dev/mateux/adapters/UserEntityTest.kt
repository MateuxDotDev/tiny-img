package dev.mateux.adapters

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("User Entity Test")
class UserEntityTest {
    @Test
    @DisplayName("Should create a user entity without id")
    fun shouldCreateAUserEntityWithoutId() {
        // Arrange
        val userEntity = UserEntity.withoutId("user", "user1@mail.com", "12345678", "salt")

        // Act
        val username = userEntity.username

        // Assert
        assertEquals("user", username)
        assertNull(userEntity.id)
    }

    @Test
    @DisplayName("Should throw an exception when try to convert an invalid user entity to domain")
    fun shouldThrowAnExceptionWhenTryToConvertAnInvalidUserEntityToDomain() {
        // Arrange
        val userEntity1 = UserEntity.test(id = null)
        val userEntity2 = UserEntity.test(username = null)
        val userEntity3 = UserEntity.test(email = null)

        // Act and Assert
        assertThrows(IllegalStateException::class.java) { userEntity1.toDomain() }
        assertThrows(IllegalStateException::class.java) { userEntity2.toDomain() }
        assertThrows(IllegalStateException::class.java) { userEntity3.toDomain() }
    }

    @Test
    @DisplayName("Should allow a user entity to be create without any parameter")
    fun shouldAllowAUserEntityToBeCreateWithoutAnyParameter() {
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
}