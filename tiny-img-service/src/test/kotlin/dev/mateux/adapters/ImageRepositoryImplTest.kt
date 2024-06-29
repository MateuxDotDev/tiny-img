package dev.mateux.adapters

import io.quarkus.test.junit.QuarkusTest
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Image Repository Implementation Test")
class ImageRepositoryImplTest {

    private lateinit var entityManager: EntityManager
    private lateinit var imageRepository: ImageRepositoryImpl

    @BeforeAll
    fun setUp() {
        entityManager = mock(EntityManager::class.java)
        imageRepository = ImageRepositoryImpl(entityManager)
    }

    @AfterEach
    fun tearDown() {
        reset(entityManager)
    }

    @Test
    fun `should throw an exception when fails to store image`() {
        // Arrange
        val imageEntity = mock(ImageEntity::class.java)
        `when`(entityManager.persist(imageEntity)).thenThrow(RuntimeException::class.java)

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            imageRepository.storeImage(imageEntity)
        }
    }

    @Test
    fun `should return null when get image by public id is not found`() {
        // Arrange
        val publicId = "public-id"
        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<ImageEntity> = mock(TypedQuery::class.java) as TypedQuery<ImageEntity>
        `when`(entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.publicId = :publicId", ImageEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("publicId", publicId)).thenReturn(typedQuery)
        `when`(typedQuery.resultList).thenReturn(emptyList())

        // Act
        val image = imageRepository.getImageByPublicId(publicId)

        // Assert
        assertNull(image)
    }

    @Test
    fun `should throw an exception when fails to get image by public id`() {
        // Arrange
        val publicId = "public-id"
        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<ImageEntity> = mock(TypedQuery::class.java) as TypedQuery<ImageEntity>
        `when`(entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.publicId = :publicId", ImageEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("publicId", publicId)).thenReturn(typedQuery)
        `when`(typedQuery.resultList).thenThrow(RuntimeException::class.java)

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            imageRepository.getImageByPublicId(publicId)
        }
    }

    @Test
    fun `should throw an exception when fails to get children images`() {
        // Arrange
        val parentId = "parent-id"
        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<ImageEntity> = mock(TypedQuery::class.java) as TypedQuery<ImageEntity>
        `when`(entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.parentId = :parentId", ImageEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("parentId", parentId)).thenReturn(typedQuery)
        `when`(typedQuery.resultList).thenThrow(RuntimeException::class.java)

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            imageRepository.getChildrenImages(parentId)
        }
    }

    @Test
    fun `should return false when removing an image does not return a updated row count greater than one`() {
        // Arrange
        val publicId = "public-id"
        val typedQuery = mock(TypedQuery::class.java)
        `when`(entityManager.createQuery("DELETE FROM ImageEntity i WHERE i.publicId = :publicId")).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("publicId", publicId)).thenReturn(typedQuery)
        `when`(typedQuery.executeUpdate()).thenReturn(0)

        // Act
        val result = imageRepository.removeImage(publicId)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `should throw an exception when remove image fails`() {
        // Arrange
        val publicId = "public-id"
        val typedQuery = mock(TypedQuery::class.java)
        `when`(entityManager.createQuery("DELETE FROM ImageEntity i WHERE i.publicId = :publicId")).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("publicId", publicId)).thenReturn(typedQuery)
        `when`(typedQuery.executeUpdate()).thenThrow(RuntimeException::class.java)

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            imageRepository.removeImage(publicId)
        }
    }

    @Test
    fun `should throw an exception when get images byu user id fails`() {
        // Arrange
        val userId = "user-id"
        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<ImageEntity> = mock(TypedQuery::class.java) as TypedQuery<ImageEntity>
        `when`(entityManager.createQuery("SELECT i FROM ImageEntity i WHERE i.userId = :userId", ImageEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("userId", userId)).thenReturn(typedQuery)
        `when`(typedQuery.resultList).thenThrow(RuntimeException::class.java)

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            imageRepository.getImagesByUserId(userId)
        }
    }
}