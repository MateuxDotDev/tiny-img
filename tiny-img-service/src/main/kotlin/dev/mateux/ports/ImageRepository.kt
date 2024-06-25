package dev.mateux.ports

import dev.mateux.adapters.ImageEntity
import dev.mateux.domain.Image

interface ImageRepository {
    fun storeImage(imageEntity: ImageEntity) : Image
}