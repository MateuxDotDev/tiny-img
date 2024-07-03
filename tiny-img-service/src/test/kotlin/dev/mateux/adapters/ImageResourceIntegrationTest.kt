package dev.mateux.adapters

import dev.mateux.domain.Roles
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.quarkus.test.security.jwt.Claim
import io.quarkus.test.security.jwt.JwtSecurity
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response.Status
import org.hamcrest.Matchers
import org.junit.jupiter.api.*

@DisplayName("Image Resource Test")
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ImageResourceIntegrationTest {
    companion object {
        val images = mutableListOf<String>()
        val children = mutableListOf<String>()
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
            .statusCode(Status.OK.statusCode)
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
            .statusCode(Status.OK.statusCode)
            .contentType(ContentType.BINARY)
    }

    @Test
    @Authenticate
    @Order(3)
    fun `should return 404 when an invalid image id is provided`() {
        // Act & Assert
        given()
            .`when`().get("/image/invalid-id")
            .then()
            .statusCode(Status.NOT_FOUND.statusCode)
    }

    @Test
    @Authenticate
    @Order(4)
    fun `should list all children from image id`() {
        // Act & Assert
        given()
            .`when`().get("/image/${images.first()}/children")
            .then()
            .statusCode(Status.OK.statusCode)
            .body("size()", Matchers.equalTo(0))
    }

    @Test
    @Authenticate
    @Order(5)
    fun `should optimize an image`() {
        // Arrange
        val payload = mapOf(
            "quality" to 50,
            "size" to 50,
            "format" to "webp"
        )

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.ACCEPTED.statusCode)

    }

    @Test
    @Authenticate
    @Order(6)
    fun `should return 404 when an invalid image id is provided to optimize`() {
        // Arrange
        val payload = mapOf(
            "quality" to 50,
            "size" to 50,
            "format" to "webp"
        )

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`().post("/image/invalid-id/optimize")
            .then()
            .statusCode(Status.NOT_FOUND.statusCode)
    }

    @Test
    @Authenticate
    @Order(7)
    fun `should return 400 when an invalid payload is provided to optimize`() {
        // Arrange
        val payload = mapOf(
            "quality" to 50,
            "size" to 50,
        )

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
    }

    @Test
    @Authenticate
    @Order(8)
    fun `should throw an exception if format is not allowed`() {
        // Arrange
        val payload = mapOf(
            "quality" to 50,
            "size" to 50,
            "format" to "mp4"
        )

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.UNSUPPORTED_MEDIA_TYPE.statusCode)
    }

    @Test
    @Authenticate
    @Order(9)
    fun `should throw an exception if quality is not between 1 and 100`() {
        // Arrange
        val payloadA = mapOf(
            "quality" to 101,
            "size" to 50,
            "format" to "webp"
        )
        val payloadB = mapOf(
            "quality" to 0,
            "size" to "50%",
            "format" to "webp"
        )

        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(payloadA)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
        given()
            .contentType(ContentType.JSON)
            .body(payloadB)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
    }

    @Test
    @Authenticate
    @Order(10)
    fun `should throw an exception if size or quality is not between 1 and 100`() {
        // Arrange
        val payloadA = mapOf(
            "quality" to 50,
            "size" to 101,
            "format" to "webp"
        )
        val payloadB = mapOf(
            "quality" to 50,
            "size" to 0,
            "format" to "webp"
        )
        val payloadC =mapOf(
            "quality" to 0,
            "size" to 50,
            "format" to "webp"
        )
        val payloadD =mapOf(
            "quality" to 101,
            "size" to 50,
            "format" to "webp"
        )


        // Act & Assert
        given()
            .contentType(ContentType.JSON)
            .body(payloadA)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
        given()
            .contentType(ContentType.JSON)
            .body(payloadB)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
        given()
            .contentType(ContentType.JSON)
            .body(payloadC)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
        given()
            .contentType(ContentType.JSON)
            .body(payloadD)
            .`when`().post("/image/${images.first()}/optimize")
            .then()
            .statusCode(Status.BAD_REQUEST.statusCode)
    }

    @Test
    @Authenticate
    @Order(11)
    fun `should return all children from image id`() {
        // Act & Assert
        given()
            .`when`().get("/image/${images.first()}/children")
            .then()
            .statusCode(Status.OK.statusCode)
            .body("size()", Matchers.equalTo(1))
    }

    @Test
    @Authenticate
    @Order(12)
    fun `should return all images from user`() {
        // Act & Assert
        given()
            .`when`().get("/image")
            .then()
            .statusCode(Status.OK.statusCode)
            .body("size()", Matchers.equalTo(2))
    }

    @Test
    @Authenticate
    @Order(13)
    fun `should remove an image`() {
        // Act & Assert
        given()
            .`when`().delete("/image/${images.first()}")
            .then()
            .statusCode(Status.NO_CONTENT.statusCode)
    }

    @Test
    @Authenticate
    @Order(14)
    fun `should return 404 when an invalid image id is provided to remove`() {
        // Act & Assert
        given()
            .`when`().delete("/image/invalid-id")
            .then()
            .statusCode(Status.NOT_FOUND.statusCode)
    }
}