package dev.mateux.domain

data class Image(
    val id: Int,
    val publicId: String,
    val path: String,
    val userId: String
) {
    companion object {
        fun test() = Image(
            id = 1,
            publicId = "publicId",
            path = "path",
            userId = "userId"
        )
    }
}