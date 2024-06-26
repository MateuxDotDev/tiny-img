package dev.mateux.adapters

import dev.mateux.domain.Roles
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.SecurityAttribute
import io.quarkus.test.security.TestSecurity
import io.restassured.http.ContentType
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.RestAssured.given
import jakarta.ws.rs.core.MediaType
import org.hamcrest.Matchers
import org.junit.jupiter.api.*

@DisplayName("Image Resource Test")
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ImageResourceIntegrationTest {
    companion object {
        val images = mutableListOf<String>()
    }

    @TestSecurity(user = "example", roles = [Roles.USER])
    @JwtSecurity(claims = [
        Claim(key = "id", value = "1"),
        Claim(key = "sub", value = "25e5be2b-6e4a-46c5-b1d6-711c8174bf3f")
    ])
    internal annotation class Authenticate

    @Test
    @Authenticate
    @Order(1)
    fun `should return a valid image id when a valid image is provided`() {
        // Arrange
        val file = ImageResourceIntegrationTest::class.java.getResourceAsStream("/image.jpeg") ?: throw Exception("File not found")

        // Act & Assert
        val imageId = given()
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .multiPart("image", "image.jpeg", file.readAllBytes(), "image/jpeg")
            .`when`().post("/image")
            .then()
            .statusCode(200)
            .body("imageId", Matchers.notNullValue())
            .body("imageId", Matchers.not(Matchers.emptyOrNullString()))
            .extract().path<String>("imageId")

        images.add(imageId)
    }

    @Test
    @Authenticate
    @Order(2)
    fun `should download a valid image id when a valid image id is provided`() {
        // Act & Assert
        given()
            .`when`().get("/image/${images.first()}")
            .then()
            .statusCode(200)
            .contentType(ContentType.BINARY)
    }

    @Test
    @Authenticate
    @Order(3)
    fun `should list all children from image id`() {
        // Act & Assert
        given()
            .`when`().get("/image/${images.first()}/children")
            .then()
            .statusCode(200)
            .body("size()", Matchers.equalTo(0))
    }
}