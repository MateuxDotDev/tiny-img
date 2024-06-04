package dev.mateux.application.util

import io.quarkus.elytron.security.common.BcryptUtil

class BcryptUtil {
    companion object {
        fun generateSalt(username: String): String {
            return BcryptUtil.bcryptHash(username)
        }

        fun generatePasswordHash(password: String, username: String, salt: String, iterationCount: Int): String? {
            return BcryptUtil.bcryptHash(password + username + salt, iterationCount)
        }

        fun validatePassword(password: String, username: String, salt: String, passwordHash: String): Boolean {
            return BcryptUtil.matches(password + username + salt, passwordHash)
        }
    }
}