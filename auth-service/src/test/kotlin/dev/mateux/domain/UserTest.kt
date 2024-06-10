package dev.mateux.domain

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

@DisplayName("User tests")
class UserTest {
    @Test
    @DisplayName("Should return true when two objects contain same values")
    fun shouldReturnFalseWhenTwoObjectsContainDifferentValues() {
        // Arrange
        val user1 = User(1, "user1", "user1@mail.com")
        val user2 = User(2, "user2", "user2@mail.com")

        // Act
        val result = user1 == user2

        // Assert
        assertFalse(result)
    }

    @Test
    @DisplayName("should return true when two objects contain same values")
    fun shouldReturnTrueWhenTwoObjectsContainSameValues() {
        // Arrange
        val user1 = User(1, "user1", "user1@mail.com")
        val user2 = User(1, "user1", "user1@mail.com")

        // Act
        val result = user1 == user2

        // Assert
        assertTrue(result)
    }
}