package dev.mateux.adapters

import dev.mateux.domain.User
import dev.mateux.ports.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException

@ApplicationScoped
class UserRepositoryImpl(
    @Inject private var entityManager: EntityManager
): UserRepository {
    override fun findByUsername(username: String): UserEntity? {
        return try {
            entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity::class.java)
            .setParameter("username", username)
            .singleResult
        } catch (e: NoResultException) {
            null
        }
    }

    override fun save(user: UserEntity): User {
        entityManager.persist(user)

        return user.toDomain()
    }
}