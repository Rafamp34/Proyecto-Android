package com.alaturing.umusicapp.main.playlist.model

import com.alaturing.umusicapp.main.song.model.Song

data class Playlist(
    val id: Int,
    val name: String,
    val author: String,
    val duration: String,
    val imageUrl: String?,
    val userId: Int,
    val songs: List<Song> = emptyList()
)