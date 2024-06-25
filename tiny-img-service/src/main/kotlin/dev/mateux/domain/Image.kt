package dev.mateux.domain

data class Image(
    val id: Int,
    val publicId: String,
    val path: String,
    val userId: String
)