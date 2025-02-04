package com.alaturing.umusicapp.main.song.model

import com.alaturing.umusicapp.authentication.model.Artist

data class Song(
    val id: Int,
    val name: String,
    val lyrics: String?,
    val album: String,
    val duration: Int,
    val imageUrl: String?,
    val artists: List<Artist> = emptyList()
)