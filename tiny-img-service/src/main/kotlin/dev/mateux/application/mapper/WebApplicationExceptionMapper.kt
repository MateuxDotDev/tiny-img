package dev.mateux.application.mapper

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class WebApplicationExceptionMapper: ExceptionMapper<WebApplicationException>  {
    override fun toResponse(exception: WebApplicationException): Response {
        return Response.status(exception.response.status)
            .entity(
                mapOf(
                    "error" to exception.message
                )
            )
            .type(MediaType.APPLICATION_JSON)
            .build()
    }
}