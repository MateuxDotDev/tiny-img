package dev.mateux.domain

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("User tests")
class UserTest {
    @Test
    fun `should return false when two objects contain same values`() {
        // Arrange
        val user1 = User(1, "user1", "user1@mail.com", "1")
        val user2 = User(2, "user2", "user2@mail.com", "2")

        // Act
        val result = user1 == user2

        // Assert
        assertFalse(result)
    }

    @Test
    @DisplayName("")
    fun `should return true when two objects contain same values`() {
        // Arrange
        val user1 = User(1, "user1", "user1@mail.com", "1")
        val user2 = User(1, "user1", "user1@mail.com", "1")

        // Act
        val result = user1 == user2

        // Assert
        assertTrue(result)
    }
}