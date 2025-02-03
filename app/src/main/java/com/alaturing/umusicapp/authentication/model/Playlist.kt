package com.alaturing.umusicapp.main.playlist.model

import com.alaturing.umusicapp.main.song.model.Song

data class Playlist(
    val id: Int,
    val name: String,
    val author: String,
    val duration: Int,
    val imageUrl: String?,
    val songs: List<Song> = emptyList()
)