package dev.mateux.ports

import dev.mateux.adapters.ImageEntity
import dev.mateux.domain.Image

interface ImageRepository {
    fun storeImage(imageEntity: ImageEntity) : Image
    fun getImageByPublicId(publicId: String): Image?
    fun getChildrenImages(parentId: String): List<Image>
    fun removeImage(publicId: String): Boolean
    fun getImagesByUserId(userId: String): List<Image>
}