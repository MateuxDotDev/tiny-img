package dev.mateux.ports

interface BCryptUtil {
    fun generateSalt(username: String): String
    fun generatePasswordHash(password: String, username: String, salt: String, iterationCount: Int): String?
    fun validatePassword(password: String, username: String, salt: String, passwordHash: String): Boolean
}