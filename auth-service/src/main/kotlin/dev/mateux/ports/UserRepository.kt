package dev.mateux.ports

import dev.mateux.adapters.UserEntity
import dev.mateux.domain.User

interface UserRepository {
    fun findByUsername(username: String): UserEntity?

    fun save(user: UserEntity): User
}