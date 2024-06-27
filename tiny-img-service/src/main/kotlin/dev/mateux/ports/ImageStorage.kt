package dev.mateux.ports

import java.io.File

interface ImageStorage {
    fun saveImage(imageIdentifier: String, image: File, extension: String, user: String): String
    fun removeImageByPath(path: String): Boolean
}