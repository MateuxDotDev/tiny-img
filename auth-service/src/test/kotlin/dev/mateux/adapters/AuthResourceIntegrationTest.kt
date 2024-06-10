package dev.mateux.adapters

import dev.mateux.domain.Roles
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import jakarta.ws.rs.core.MediaType
import org.hamcrest.Matchers
import org.junit.jupiter.api.*


@DisplayName("Auth Resource Test")
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthResourceIntegrationTest {
    @Test
    @Order(1)
    @DisplayName("should return a token when a valid user is registered")
    fun shouldReturnTokenWhenValidUserIsRegistered() {
        // Arrange
        val payload = mapOf(
            "username" to "test",
            "password" to "1234Abc#",
            "email" to "test@mail.com"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/register")
            .then()
            .statusCode(200)
            .body("token", Matchers.notNullValue())
            .body("token", Matchers.not(Matchers.emptyOrNullString()))
    }

    @Test
    @Order(2)
    @DisplayName("should throw an exception when a user with the same username is registered")
    fun shouldThrowExceptionWhenUserWithSameUsernameIsRegistered() {
        // Arrange
        val payload = mapOf(
            "username" to "test",
            "password" to "1234Abc#",
            "email" to "test@mail.com"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/register")
            .then()
            .statusCode(400)
            .body("error", Matchers.equalTo("Username or email already exists"))
    }

    @Test
    @Order(3)
    @DisplayName("should thrown an exception when a user with the same email is registered")
    fun shouldThrowExceptionWhenUserWithSameEmailIsRegistered() {
        // Arrange
        val payload = mapOf(
            "username" to "test2",
            "password" to "1234Abc#",
            "email" to "test@mail.com"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/register")
            .then()
            .statusCode(400)
            .body("error", Matchers.equalTo("Username or email already exists"))
    }

    @Test
    @Order(4)
    @DisplayName("should throw an exception when password is too weak")
    fun shouldThrowExceptionWhenPasswordIsTooWeak() {
        // Arrange
        val payload = mapOf(
            "username" to "test2",
            "password" to "12345678",
            "email" to "test@mail.com"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/register")
            .then()
            .statusCode(400)
            .body("error", Matchers.equalTo("Password must be at least 8 characters long and include a mix of uppercase, lowercase, digit, and special character."))
    }

    @Test
    @Order(5)
    @DisplayName("should return a token when a valid user logs in")
    fun shouldReturnTokenWhenValidUserLogsIn() {
        // Arrange
        val payload = mapOf(
            "username" to "test",
            "password" to "1234Abc#"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", Matchers.notNullValue())
            .body("token", Matchers.not(Matchers.emptyOrNullString()))
    }

    @Test
    @Order(6)
    @DisplayName("should throw an exception when a user is not found")
    fun shouldThrowExceptionWhenUserIsNotFound() {
        // Arrange
        val payload = mapOf(
            "username" to "test3",
            "password" to "1234Abc#"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/login")
            .then()
            .statusCode(404)
            .body("error", Matchers.equalTo("User not found"))
    }

    @Test
    @Order(7)
    @DisplayName("should throw an exception when user password is wrong")
    fun shouldThrowExceptionWhenUserPasswordIsWrong() {
        // Arrange
        val payload = mapOf(
            "username" to "test",
            "password" to "1234Abc#1"
        )

        // Act & Assert
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/login")
            .then()
            .statusCode(401)
            .body("error", Matchers.equalTo("Invalid password"))
    }

    @Test
    @Order(8)
    @DisplayName("should return username, id, groups when a authorized user accesses me")
    fun shouldReturnUsernameIdGroupsWhenAuthorizedUserAccessesMe() {
        // Arrange
        val payload = mapOf(
            "username" to "test",
            "password" to "1234Abc#"
        )
        val token = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .`when`().post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path<String>("token")

        // Act & Assert
        given()
            .header("Authorization", "Bearer $token")
            .`when`().get("/auth/me")
            .then()
            .statusCode(200)
            .body("username", Matchers.equalTo("test"))
            .body("id", Matchers.notNullValue())
            .body("groups", Matchers.hasSize<Int>(1))
            .body("groups[0]", Matchers.equalTo(Roles.USER))
    }

    @Test
    @Order(9)
    @DisplayName("should throw an exception when an unauthorized user accesses me")
    fun shouldThrowExceptionWhenUnauthorizedUserAccessesMe() {
        // Act & Assert
        given()
            .`when`().get("/auth/me")
            .then()
            .statusCode(401)
    }
}