package dev.mateux.application.dto

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty

@SchemaProperty(name = "User login payload", example = "{\n  \"username\": \"cooper\",\n  \"password\": \"password\"\n}", nullable = false, type = SchemaType.OBJECT)
data class UserLoginDto(
    @SchemaProperty(name = "Username", type = SchemaType.STRING )
    val username: String,
    @SchemaProperty(name = "Password", type = SchemaType.STRING, format = "password")
    val password: String
)
