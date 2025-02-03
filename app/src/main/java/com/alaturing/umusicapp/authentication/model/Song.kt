package com.alaturing.umusicapp.main.song.model

data class Song(
    val id: Int,
    val name: String,
    val author: String,
    val album: String,
    val duration: Int,
    val imageUrl: String?
)