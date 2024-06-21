package dev.mateux.adapters

import dev.mateux.ports.BCryptUtil
import io.quarkus.elytron.security.common.BcryptUtil
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class BcryptUtilImpl : BCryptUtil {
    override fun generateSalt(username: String): String {
        return BcryptUtil.bcryptHash(username)
    }

    override fun generatePasswordHash(password: String, username: String, salt: String, iterationCount: Int): String? {
        return BcryptUtil.bcryptHash(password + username + salt, iterationCount)
    }

    override fun validatePassword(password: String, username: String, salt: String, passwordHash: String): Boolean {
        return BcryptUtil.matches(password + username + salt, passwordHash)
    }
}