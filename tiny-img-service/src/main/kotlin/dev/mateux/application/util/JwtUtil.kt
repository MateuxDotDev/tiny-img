package dev.mateux.application.util

import dev.mateux.domain.Roles
import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.Claims

@ApplicationScoped
class JwtUtil(
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://example.com/issuer") private var issuer: String,
    @ConfigProperty(name = "mp.jwt.verify.expires-in-seconds", defaultValue = "300")  private var expiresIn: String
) {
    fun generateToken(username: String, userId: String): String {
        return Jwt.issuer(issuer)
            .preferredUserName(username)
            .claim(Claims.sub, userId)
            .groups(Roles.USER)
            .expiresIn(expiresIn.toLong())
            .sign()
    }
}