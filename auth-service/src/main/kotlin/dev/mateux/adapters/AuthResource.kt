package dev.mateux.adapters

import dev.mateux.application.AuthService
import dev.mateux.application.dto.UserLoginDto
import dev.mateux.application.dto.UserRegisterDto
import dev.mateux.domain.Roles
import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement

@RequestScoped
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AuthResource(
    @Inject private var authService: AuthService
) {
    @POST
    @Path("/login")
    @PermitAll
    @RunOnVirtualThread
    @RequestBody(
        name = "User login payload",
        description = "User login payload containing username and password",
        required = true,
        content = [Content(mediaType = "application/json")],
    )
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "User authenticated successfully"),
            APIResponse(responseCode = "401",  description = "Invalid credentials"),
            APIResponse(responseCode = "404", description = "User not found"),
            APIResponse(responseCode = "500", description = "User has no password"),
            APIResponse(responseCode = "500", description = "User has no salt"),
            APIResponse(responseCode = "500", description = "User has no publicId")
        ]
    )
    fun login(data: UserLoginDto): Response {
        val token = authService.authenticate(data.username, data.password)

        return Response.ok(
            mapOf("token" to token)
        ).build()
    }

    @POST
    @Path("/register")
    @PermitAll
    @RunOnVirtualThread
    @RequestBody(
        name = "User register payload",
        description = "User register payload containing username, email and password",
        required = true,
    )
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "User registered successfully"),
            APIResponse(responseCode = "400", description = "Password is not strong enough"),
            APIResponse(responseCode = "400", description = "Username or email already exists"),
            APIResponse(responseCode = "500", description = "Failed to hash password"),
            APIResponse(responseCode = "500", description = "User has no publicId")
        ]
    )
    fun register(data: UserRegisterDto): Response {
        val token = authService.register(data.username, data.email, data.password)

        return Response.ok(
            mapOf("token" to token)
        ).build()
    }

    @GET
    @Path("/me")
    @RolesAllowed(Roles.USER)
    @RunOnVirtualThread
    @SecurityRequirement(name = "bearerAuth")
    @APIResponses(
        value = [
            APIResponse(responseCode = "200", description = "User information"),
            APIResponse(responseCode = "401", description = "Unauthorized")
        ]
    )
        fun me(@Context ctx: SecurityContext): Response {
        val token = ctx.userPrincipal as JsonWebToken
        return Response.ok(
            mapOf(
                "username" to token.name,
                "id" to token.getClaim("sub"),
                "groups" to token.getClaim("groups")
            )
        ).build()
    }
}