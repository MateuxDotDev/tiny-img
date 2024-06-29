package dev.mateux.adapters

import dev.mateux.application.ImageService
import dev.mateux.application.dto.OptimizationOptions
import dev.mateux.application.util.UploadItemSchema
import dev.mateux.domain.Roles
import dev.mateux.domain.User
import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
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
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RunOnVirtualThread
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "Image uploaded successfully",
            ),
            APIResponse(
                responseCode = "413",
                description = "File too large"
            ),
            APIResponse(
                responseCode = "415",
                description = "File type not allowed"
            ),
            APIResponse(
                responseCode = "500",
                description = "Error saving image"
            )
        ]
    )
    fun uploadImage(
        @Context context: SecurityContext,
        @Schema(implementation = UploadItemSchema::class, name = "Uplooad Item Schema") @RestForm("image") image: FileUpload
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
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "Image retrieved successfully",
            ),
            APIResponse(
                responseCode = "404",
                description = "Image not found"
            ),
            APIResponse(
                responseCode = "500",
                description = "Error retrieving image"
            )
        ]
    )
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
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "Children images retrieved successfully",
            ),
            APIResponse(
                responseCode = "404",
                description = "Image not found"
            ),
            APIResponse(
                responseCode = "500",
                description = "Error retrieving children images"
            )
        ]
    )
    fun getChildrenImages(
        @PathParam("imageId") imageId: String
    ) : Response {
        return Response.ok(
            imageService.getChildrenImages(imageId)
        ).build()
    }

    @POST
    @Path("/{imageId}/optimize")
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "202",
                description = "Image optimization started",
            ),
            APIResponse(
                responseCode = "400",
                description = "Invalid optimization options"
            ),
            APIResponse(
                responseCode = "404",
                description = "Image not found"
            ),
            APIResponse(
                responseCode = "415",
                description = "Format not allowed"
            ),
            APIResponse(
                responseCode = "500",
                description = "Error optimizing image"
            )
        ]
    )
    fun optimizeImage(
        @PathParam("imageId") imageId: String,
        options: OptimizationOptions
    ) : Response {
        return if (imageService.optimizeImage(imageId, options)) Response.accepted().build() else Response.serverError().build()
    }

    @GET
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "All user images retrieved successfully",
            ),
            APIResponse(
                responseCode = "500",
                description = "Error retrieving user images"
            )
        ]
    )
    fun getMyImages(
    @Context context: SecurityContext
    ) : Response {
        return Response.ok(
            imageService.getMyImages(User.fromSecurityContext(context))
        ).build()
    }

    @DELETE
    @Path("/{imageId}")
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "204",
                description = "Image deleted successfully",
            ),
            APIResponse(
                responseCode = "404",
                description = "Image not found"
            ),
            APIResponse(
                responseCode = "500",
                description = "Error deleting image"
            )
        ]
    )
    fun deleteImage(
        @PathParam("imageId") imageId: String
    ) : Response {
        return if (imageService.removeImage(imageId)) Response.noContent().build() else Response.serverError().build()
    }
}