package dev.mateux.application.dto

data class QueuePayload(
    val originalImagePath: String,
    val user: String,
    val imageId: String,
    val size: String,
    val format: String,
    val quality: Int,
)
