package dev.mateux.domain

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class UserTest {
    @Test
    fun shouldReturnFalseWhenTwoObjectsContainDifferentValues() {
        // Arrange
        val user1 = User(1, "user1", "user1@mail.com")
        val user2 = User(2, "user2", "user2@mail.com")

        // Act
        val result = user1 == user2

        // Assert
        assertFalse(result)
    }
}