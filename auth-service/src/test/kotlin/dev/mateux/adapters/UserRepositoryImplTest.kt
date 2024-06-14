package dev.mateux.adapters

import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.TypedQuery
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("User Repository Impl Test")
class UserRepositoryImplTest {

    private lateinit var entityManager: EntityManager
    private lateinit var userRepository: UserRepositoryImpl

    @BeforeAll
    fun setUp() {
        entityManager = mock(EntityManager::class.java)
        userRepository = UserRepositoryImpl(entityManager)
    }

    @Test
    @DisplayName("findByUserName should return a single user entity")
    fun findByUserNameShouldReturnASingleUserEntity() {
        // Arrange
        val username = "user1"
        val userEntity = UserEntity.test()

        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<UserEntity> = mock(TypedQuery::class.java) as TypedQuery<UserEntity>
        `when`(entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("username", username)).thenReturn(typedQuery)
        `when`(typedQuery.singleResult).thenReturn(userEntity)

        `when`(entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity::class.java).setParameter("username", username).singleResult).thenReturn(userEntity)

        // Act
        val result = userRepository.findByUsername(username)

        // Assert
        assertNotNull(result)
        assertEquals(userEntity, result)
    }

    @Test
    @DisplayName("findByUserName should return null when user not found")
    fun findByUserNameShouldReturnNullWhenUserNotFound() {
        // Arrange
        val username = "user1"

        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<UserEntity> = mock(TypedQuery::class.java) as TypedQuery<UserEntity>
        `when`(entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("username", username)).thenReturn(typedQuery)
        `when`(typedQuery.singleResult).thenThrow(NoResultException::class.java)

        // Act
        val result = userRepository.findByUsername(username)

        // Assert
        assertNull(result)
    }

    @Test
    @DisplayName("findByUserName should throw exception when query fails")
    fun findByUserNameShouldThrowExceptionWhenQueryFails() {
        // Arrange
        val username = "user1"

        @Suppress("unchecked_cast")
        val typedQuery: TypedQuery<UserEntity> = mock(TypedQuery::class.java) as TypedQuery<UserEntity>
        `when`(entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity::class.java)).thenReturn(typedQuery)
        `when`(typedQuery.setParameter("username", username)).thenReturn(typedQuery)
        `when`(typedQuery.singleResult).thenThrow(RuntimeException::class.java)

        // Act and Assert
        assertThrows(RuntimeException::class.java) {
            userRepository.findByUsername(username)
        }
    }

    @Test
    @DisplayName("Save should persist user entity")
    fun saveShouldPersistUserEntity() {
        // Arrange
        val userEntity = UserEntity.test()

        // Act
        userRepository.save(userEntity)

        // Assert
        // Verify if the persist method was called
        verify(entityManager, times(1)).persist(userEntity)
    }
}