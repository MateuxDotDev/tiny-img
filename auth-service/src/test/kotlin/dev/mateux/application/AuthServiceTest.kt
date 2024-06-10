package dev.mateux.application

import dev.mateux.adapters.UserEntity
import dev.mateux.application.util.JwtUtil
import dev.mateux.ports.BCryptUtil
import dev.mateux.ports.UserRepository
import jakarta.ws.rs.WebApplicationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Auth Service Test")
class AuthServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var jwtUtil: JwtUtil
    private lateinit var bcryptUtil: BCryptUtil
    private lateinit var authService: AuthService

    @BeforeAll
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        jwtUtil = mock(JwtUtil::class.java)
        bcryptUtil = mock(BCryptUtil::class.java)
        authService = AuthService(userRepository, jwtUtil, 10.toString(), bcryptUtil)
    }

    @Test
    @DisplayName("authenticate should return a token when user is authenticated")
    fun authenticateShouldReturnATokenWhenUserIsAuthenticated() {
        // Arrange
        val password = "1234Abc#"
        val publicId = "1"
        val salt = "salt"
        val passwordHash = "passwordHash"
        val testUser = UserEntity.test(password = passwordHash)
        val userDomain = testUser.toDomain()
        `when`(userRepository.findByUsername(userDomain.username)).thenReturn(testUser)
        `when`(bcryptUtil.validatePassword(password, userDomain.username, salt, passwordHash)).thenReturn(true)
        `when`(jwtUtil.generateToken(userDomain.username, publicId)).thenReturn("token")

        // Act
        val result = authService.authenticate(userDomain.username, password)

        // Assert
        assertNotNull(result)
        assertEquals("token", result)
    }

    @Test
    @DisplayName("authenticate should throw an exception when user is not found")
    fun authenticateShouldThrowAnExceptionWhenUserIsNotFound() {
        // Arrange
        val username = "username"
        val password = "1234Abc#"
        `when`(userRepository.findByUsername(username)).thenReturn(null)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            authService.authenticate(username, password)
        }

        // Assert
        assertEquals("User not found", exception.message)
        assertEquals(404, exception.response.status)
    }

    @Test
    @DisplayName("authenticate should throw an exception when user has no password hash")
    fun authenticateShouldThrowAnExceptionWhenUserHasNoPasswordHash() {
        // Arrange
        val password = "1234Abc#"
        val testUser = UserEntity.test(password = null)
        val userDomain = testUser.toDomain()
        `when`(userRepository.findByUsername(userDomain.username)).thenReturn(testUser)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            authService.authenticate(userDomain.username, password)
        }

        // Assert
        assertEquals("User has no password", exception.message)
        assertEquals(500, exception.response.status)
    }

    @Test
    @DisplayName("authenticate should throw an exception when user has no salt")
    fun authenticateShouldThrowAnExceptionWhenUserHasNoSalt() {
        // Arrange
        val username = "username"
        val password = "1234Abc#"
        val testUser = UserEntity.test(salt = null)
        `when`(userRepository.findByUsername(username)).thenReturn(testUser)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            authService.authenticate(username, password)
        }

        // Assert
        assertEquals("User has no salt", exception.message)
        assertEquals(500, exception.response.status)
    }

    @Test
    @DisplayName("authenticate should throw an exception when user has no public id")
    fun authenticateShouldThrowAnExceptionWhenUserHasNoPublicId() {
        // Arrange
        val username = "username"
        val password = "1234Abc#"
        val testUser = UserEntity.test(publicId = null)
        `when`(userRepository.findByUsername(username)).thenReturn(testUser)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            authService.authenticate(username, password)
        }

        // Assert
        assertEquals("User has no publicId", exception.message)
        assertEquals(500, exception.response.status)
    }

    @Test
    @DisplayName("authenticate should throw an exception when password is invalid")
    fun authenticateShouldThrowAnExceptionWhenPasswordIsInvalid() {
        // Arrange
        val username = "username"
        val password = "1234Abc#"
        val salt = "salt"
        val passwordHash = "passwordHash"
        val testUser = UserEntity.test(username = username, password = passwordHash)
        val userDomain = testUser.toDomain()
        `when`(userRepository.findByUsername(userDomain.username)).thenReturn(testUser)
        `when`(bcryptUtil.validatePassword(password, userDomain.username, salt, passwordHash)).thenReturn(false)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            authService.authenticate(username, password)
        }

        // Assert
        assertEquals("Invalid password", exception.message)
        assertEquals(401, exception.response.status)
    }
}