package dev.mateux.adapters

import dev.mateux.application.ImageService
import dev.mateux.domain.Roles
import dev.mateux.domain.User
import io.smallrye.common.annotation.RunOnVirtualThread
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.io.File


@Path("/image")
@Tag(name = "Image")
@RequestScoped
@RolesAllowed(Roles.USER)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ImageResource(
    @Inject private var imageService: ImageService
) {
    @Schema(type = SchemaType.STRING, format = "binary")
    internal class UploadItemSchema

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    fun uploadImage(
        @Context context: SecurityContext,
        @Schema(implementation = UploadItemSchema::class) @RestForm("image") image: FileUpload
    ) : Response {
        return Response.ok(
            mapOf(
                "imageId" to imageService.uploadImage(image, User.fromSecurityContext(context))
            )
        ).build()
    }

    @GET
    @Path("/{imageId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getImage(
        @PathParam("imageId") imageId: String
    ) : Response {
        val imageFile: File = imageService.getImage(imageId)

        return Response.ok(imageFile.inputStream())
            .header("Content-Disposition", "attachment; filename=\"${imageFile.name}\"")
            .build()
    }

    @GET
    @Path("/{imageId}/children")
    fun getChildrenImages(
        @PathParam("imageId") imageId: String
    ) : Response {
        return Response.ok(
            imageService.getChildrenImages(imageId)
        ).build()
    }
}