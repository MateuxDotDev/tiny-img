package dev.mateux.application.dto

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty

@SchemaProperty(name = "User register payload", example = "{\n  \"username\": \"cooper\",\n  \"email\": \"cooper@mail.com\",\n  \"password\": \"password\"\n}", nullable = false)
class UserRegisterDto(
    @SchemaProperty(name = "Username", type = SchemaType.STRING )
    val username: String,
    @SchemaProperty(name = "Password", type = SchemaType.STRING, format = "password")
    val password: String,
    @SchemaProperty(name = "Email", type = SchemaType.STRING)
    val email: String
)