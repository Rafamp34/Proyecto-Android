package com.alaturing.umusicapp.authentication.data.remote.model

data class CreatePlaylistBody(
    val data: CreatePlaylistData
)

data class CreatePlaylistData(
    val name: String,
    val author: String,
    val duration: String = "0",
    val image: Int? = null
)

data class UploadResponse(
    val id: Int,
    val url: String
)
