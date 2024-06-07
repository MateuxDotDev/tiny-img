package dev.mateux.adapters

import dev.mateux.application.AuthService
import dev.mateux.application.dto.UserLoginDto
import dev.mateux.application.dto.UserRegisterDto
import dev.mateux.domain.Roles
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.jwt.JsonWebToken
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.extensions.Extension
import org.eclipse.microprofile.openapi.annotations.info.Contact
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.info.License
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.eclipse.microprofile.openapi.annotations.tags.Tag

@ApplicationScoped
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
    info = Info(
        title = "Auth API",
        version = "1.0.0",
        extensions = [Extension(name = "x-logo", value = "https://github.com/MateuxDotDev/tiny-img/raw/main/assets/logo.png")],
        description = "API for user authentication and registration",
        license = License(name = "x-license", url = "https://github.com/MateuxDotDev/tiny-img/blob/main/LICENSE"),
        contact = Contact(
            name = "Mateus Lucas",
            url = "mateux.dev"
        ),
    )
)
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
class AuthResource(
    @Inject private var authService: AuthService
) {
    @POST
    @Path("/login")
    @PermitAll
    @RequestBody(
        name = "User login payload",
        description = "User login payload containing username and password",
        required = true,
        extensions = [Extension(
            name = "x-example",
            value = "{\n  \"username\": \"cooper\",\n  \"password\": \"password\"\n}"
        )],
        content = [Content(mediaType = "application/json")],
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User authenticated successfully",
                content = [Content(mediaType = "application/json")],
            ),
            APIResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "500",
                description = "User has no password",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "500",
                description = "User has no salt",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "500",
                description = "User has no publicId",
                content = [Content(mediaType = "application/json")]
            )
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
    @RequestBody(
        name = "User register payload",
        description = "User register payload containing username, email and password",
        required = true,
        extensions = [Extension(
            name = "x-example",
            value = "{\n  \"username\": \"cooper\",\n  \"email\": \"cooper@mail.com\",\n  \"password\": \"password\"\n}"
        )],
        content = [Content(mediaType = "application/json")]
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User registered successfully",
                content = [Content(mediaType = "application/json")],
            ),
            APIResponse(
                responseCode = "400",
                description = "Password is not strong enough",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "400",
                description = "Username or email already exists",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "500",
                description = "Failed to hash password",
                content = [Content(mediaType = "application/json")]
            ),
            APIResponse(
                responseCode = "500",
                description = "User has no publicId",
                content = [Content(mediaType = "application/json")]
            )
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
    @SecurityRequirement(name = "bearerAuth")
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User information",
                content = [Content(mediaType = "application/json")],
            ),
            APIResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(mediaType = "application/json")]
            )
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