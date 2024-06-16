package dev.mateux.application

import dev.mateux.adapters.UserEntity
import dev.mateux.application.util.JwtUtil
import dev.mateux.ports.BCryptUtil
import dev.mateux.ports.UserRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.WebApplicationException
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class AuthService(
    @Inject private var userRepository: UserRepository,
    @Inject private var jwtUtil: JwtUtil,
    @ConfigProperty(name = "bcrypt.iteration-count", defaultValue = "14") private var iterationCount: String,
    @Inject private var bcryptUtilImpl: BCryptUtil,
) {
    private val strongPasswordRegex: Regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}\$")

    fun authenticate(username: String, password: String): String {
        val user = userRepository.findByUsername(username) ?: throw WebApplicationException("User not found", 404)

        val passwordHash = user.password ?: throw WebApplicationException("User has no password", 500)
        val salt = user.salt ?: throw WebApplicationException("User has no salt", 500)
        val publicId = user.publicId ?: throw WebApplicationException("User has no publicId", 500)

        if (bcryptUtilImpl.validatePassword(password, username, salt, passwordHash)) {
            return jwtUtil.generateToken(username, publicId)
        } else {
            throw WebApplicationException("Invalid password", 401)
        }
    }

    @Transactional(rollbackOn = [Exception::class], value = Transactional.TxType.REQUIRED)
    fun register(username: String, email: String, password: String): String {
        if (!isPasswordStrong(password)) throw WebApplicationException("Password must be at least 8 characters long and include a mix of uppercase, lowercase, digit, and special character.", 400)

        val salt = bcryptUtilImpl.generateSalt(username)
        val passwordHash = bcryptUtilImpl.generatePasswordHash(password, username, salt, iterationCount.toInt()) ?: throw WebApplicationException("Failed to hash password", 500)

        val user = UserEntity.withoutId(username, email, passwordHash, salt)

        val userDomain = try {
            userRepository.save(user)
        } catch (e: Exception) {
            if (e.message?.contains("unique constraint") == true) {
                throw WebApplicationException("Username or email already exists", 400)
            }

            throw WebApplicationException("Failed to save user", 500)
        }

        return jwtUtil.generateToken(username, userDomain.publicId)
    }

    private fun isPasswordStrong(password: String): Boolean {
        return strongPasswordRegex.matches(password)
    }
}
