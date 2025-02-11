package com.alaturing.umusicapp.authentication.model

import com.alaturing.umusicapp.main.song.model.Song

data class Artist(
    val id: Int,
    val name: String,
    val listeners: String,
    val imageUrl: String?,
    val songs_IDS: List<Song> = emptyList()
)