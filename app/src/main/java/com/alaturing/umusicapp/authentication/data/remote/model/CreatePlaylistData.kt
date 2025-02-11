package com.alaturing.umusicapp.authentication.data.remote.model

data class CreatePlaylistBody(
    val data: CreatePlaylistData
)

data class CreatePlaylistData(
    val name: String,
    val author: String,
    val duration: String = "0",
    val image: ImageReference? = null
)

data class ImageReference(
    val connect: List<Int>
)

data class UploadResponse(
    val id: Int,
    val url: String
)
