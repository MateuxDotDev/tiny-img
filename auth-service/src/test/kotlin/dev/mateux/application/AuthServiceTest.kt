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
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import java.sql.SQLException

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
    fun `authenticate should return a token when user is authenticated`() {
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
    fun `authenticate should throw an exception when user is not found`() {
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
    fun `authenticate should throw an exception when user has no password hash`() {
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
    fun `authenticate should throw an exception when user has no salt`() {
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
    fun `authenticate should throw an exception when user has no public id`() {
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
    fun `authenticate should throw an exception when password is invalid`() {
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

    @Test
    fun `register should return a token when user is registered`() {
        // Arrange
        val username = "username"
        val email = "email"
        val password = "1234Abc#"
        val salt = "salt"
        val passwordHash = "passwordHash"
        val testUser = UserEntity.test(username = username, email = email, password = passwordHash, salt = salt, publicId = "1")
        val userDomain = testUser.toDomain()
        `when`(bcryptUtil.generateSalt(username)).thenReturn(salt)
        `when`(bcryptUtil.generatePasswordHash(password, username, salt, 10)).thenReturn(passwordHash)
        `when`(userRepository.save(anyOrNull())).thenReturn(userDomain)
        `when`(jwtUtil.generateToken(anyString(), anyString())).thenReturn("token")

        // Act
        val result = authService.register(username, email, password)

        // Assert
        assertNotNull(result)
        assertEquals("token", result)
    }

    @Test
    fun `register should throw an exception when password fails to hash`() {
        // Arrange
        val username = "username"
        val email = "email"
        val password = "1234Abc#"
        val salt = "salt"
        `when`(bcryptUtil.generateSalt(username)).thenReturn(salt)
        `when`(bcryptUtil.generatePasswordHash(password, username, salt, 10)).thenReturn(null)

        // Act
        val exception = assertThrows(WebApplicationException::class.java) {
            authService.register(username, email, password)
        }

        // Assert
        assertEquals("Failed to hash password", exception.message)
        assertEquals(500, exception.response.status)
    }

//    @Test
//    fun `register should rethrow exception on save when message is null`() {
//        // Arrange
//        val username = "username"
//        val email = "email"
//        val password = "1234Abc#"
//        val salt = "salt"
//        val passwordHash = "passwordHash"
//        `when`(bcryptUtil.generateSalt(username)).thenReturn(salt)
//        `when`(bcryptUtil.generatePasswordHash(password, username, salt, 10)).thenReturn(passwordHash)
//        val mockException = mock(Exception::class.java)
//        `when`(userRepository.save(anyOrNull())).thenThrow(mockException)
//        `when`(mockException.message).thenReturn(null)
//
//        // Act
//        val exception = assertThrows(WebApplicationException::class.java) {
//            authService.register(username, email, password)
//        }
//
//        // Assert
//        assertNull(exception.message)
//        assertEquals(500, exception.response.status)
//        assertEquals("Failed to save user", exception.message)
//    }
//
//    @Test
//    fun `register should rethrow exception on save when message is not unique constraint`() {
//        // Arrange
//        val username = "username"
//        val email = "email"
//        val password = "1234Abc#"
//        val salt = "salt"
//        val passwordHash = "passwordHash"
//        val testUser = UserEntity.test(username = username, email = email, password = passwordHash, salt = salt)
//        `when`(bcryptUtil.generateSalt(username)).thenReturn(salt)
//        `when`(bcryptUtil.generatePasswordHash(password, username, salt, 10)).thenReturn(passwordHash)
//        `when`(userRepository.save(anyOrNull())).thenThrow(SQLException("generic exception without any relevant message"))
//
//        // Act
//        val exception = assertThrows(WebApplicationException::class.java) {
//            authService.register(username, email, password)
//        }
//
//        // Assert
//        assertEquals("Failed to save user", exception.message)
//        assertEquals(500, exception.response.status)
//    }
}