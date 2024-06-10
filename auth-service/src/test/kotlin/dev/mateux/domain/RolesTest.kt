package dev.mateux.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Roles")
class RolesTest {
    @Test
    @DisplayName("Should return the user role")
    fun shouldReturnTheUserRole() {
        // Act & Assert
        assertEquals("user", Roles.USER)
    }
}